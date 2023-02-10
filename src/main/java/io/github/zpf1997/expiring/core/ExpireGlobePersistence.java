package io.github.zpf1997.expiring.core;

import ch.qos.logback.core.util.CloseUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;


/**
 * <p>
 * Expiring - map persistence mechanism
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpireGlobePersistence<K, V> extends AbstractGlobePersistenceIndicator<K, V> implements
        GlobePersistence<K, V>, Serializable {

    private static final long serialVersionUID = 7755214068683107950L;

    private static ExpireMapCacheProperties cacheProperties;

    private static final String PREFIX_BEFORE = ".aof";

    private static final String AT = "@";

    private Persistence<K, V> persistence;

    @Getter
    private String writePath;

    private static final String DEALT = "$*&";

    private static boolean OPEN_PERSISTENCE;

    private static final Map<String, ExpireGlobePersistence> CACHE_MAP = new ConcurrentHashMap<>();

    public static final ExpireGlobePersistence<Object, Object> INSTANCE = new ExpireGlobePersistence<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private ExpireGlobePersistence() {
    }

    @SuppressWarnings("unused")
    public ExpireGlobePersistence(Persistence<K, V> persistence, String writePath) {
        Assert.notNull(writePath, "writePath no be null");
        this.writePath = writePath;
        Assert.notNull(persistence, "Persistence no be null !");
        this.persistence = persistence;
    }

    public ExpireGlobePersistence(Supplier<Persistence<K, V>> persistence, String writePath) {
        Assert.notNull(writePath, "writePath no be null");
        this.writePath = writePath;
        Persistence<K, V> p = persistence.get();
        Assert.notNull(p, "Persistence no be null !");
        this.persistence = p;
    }

    static {
        try {
            resolveCacheProperties();
        } catch (Exception e) {
            io.github.zpf1997.expiring.core.Console.logger.error("load CacheProperties error {}", e.getMessage());
        }
    }

    public static void resolveCacheProperties() {
        cacheProperties = Application.context.getBean(ExpireMapCacheProperties.class);
        OPEN_PERSISTENCE = cacheProperties.getPersistence().getOpenPersistence();
        checkDirectory();
    }

    public static void checkDirectory() {
        String persistencePath = cacheProperties.getPersistence().getPersistencePath();
        if (StringUtils.isNotBlank(cacheProperties.getPersistence().getPersistencePath())) {
            //Determine whether to native folders and whether the folder
            if (!isDirectory(persistencePath)) {
                File directory = mkdir(persistencePath);
                if (!directory.exists()) {
                    checkError(persistencePath);
                }
            }
        }
    }

    private static void checkError(String persistencePath) {
        String[] pathArray = persistencePath.split("/");
        Assert.isTrue(ArrayUtil.isNotEmpty(pathArray),
                "[" + persistencePath + "] no a path");
        String line = "";
        for (String path : pathArray) {
            if (StringUtils.isBlank(path)) {
                continue;
            }
            line += "/" + path;
            Assert.isTrue(isDirectory(line),
                    "[" + line + "] no a Directory for your file system");
        }
    }

    public static <K, V> ExpireGlobePersistence<K, V> of(Entry<K, V> entry, String factoryBeanName) {
        Assert.notNull(entry, "Entry no be null");
        Assert.isTrue(StringUtils.isNotBlank(factoryBeanName), "factoryBeanName no be blank");
        checkOf(entry);
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        String writePath = rawWritePath(entry.getKey());
        ExpireGlobePersistence<K, V> persistence = CACHE_MAP.get(rawHash);
        if (persistence == null) {
            synchronized (ExpireGlobePersistence.class) {
                persistence = CACHE_MAP.get(rawHash);
                if (persistence == null) {
                    CACHE_MAP.putIfAbsent(rawHash,
                            new ExpireGlobePersistence<>(() -> Persistence.of(entry).expireOn(factoryBeanName)
                                    , writePath));
                    persistence = CACHE_MAP.get(rawHash);
                }
            }
        }
        return persistence;
    }

    public static <K, V> ExpireGlobePersistence<K, V> of(Persistence<K, V> persistence) {
        Assert.notNull(persistence, "Persistence no be null");
        Entry<K, V> entry = persistence.entry;
        //To recalculate the path
        String writePath = rawWritePath(entry.getKey());
        return new ExpireGlobePersistence<>(() -> persistence, writePath);
    }

    private static <V, K> void checkOf(Entry<K, V> entry) {
        Assert.isTrue(OPEN_PERSISTENCE, "No open expiring persistence");
        Assert.notNull(entry, "Entry no be null");
        //empty just pass
        if (entry.getDuration() == null || entry.getTimeUnit() == null) {
            return;
        }
        if ((entry.getDuration() != null && entry.getDuration() >=
                cacheProperties.getPersistence().getNoPersistenceOfExpireTime())
                && (entry.getTimeUnit() != null && entry.getTimeUnit()
                .equals(cacheProperties.getPersistence().getNoPersistenceOfExpireTimeUnit()))) {
            return;
        }
        throw new IllegalArgumentException("Only more than or == " +
                cacheProperties.getPersistence().getNoPersistenceOfExpireTime() + " " +
                cacheProperties.getPersistence().getNoPersistenceOfExpireTimeUnit() +
                " can be persisted so key [" + entry.getKey() + "] value [" + entry.getValue() + "] no persisted ");
    }

    public static <K, V> ExpireGlobePersistence<K, V> of(@NonNull K key, @NonNull V value) {
        Assert.isTrue(OPEN_PERSISTENCE, "No open expiring persistence");
        ExpireGlobePersistence<K, V> expireGlobePersistence = CACHE_MAP.get(rawHash(key, value));
        if (expireGlobePersistence == null) {
            throw new BeanDefinitionStoreException("Key: " + key + " Value: " + value + " raw hash no found " +
                    "expireGlobePersistence");
        }
        return expireGlobePersistence;
    }

    public static <K> String rawWritePath(@NonNull K key) {
        Assert.notNull(key, "Key no be null ");
        return cacheProperties.getPersistence().getPersistencePath() + key + PREFIX_BEFORE;
    }

    public static <K, V> String rawHash(@NonNull K key, @NonNull V value) {
        return DEALT + rawHash(key) + rawHash(value);
    }

    public static <T> String rawHash(@NonNull T t) {
        Assert.notNull(t, "T no be null");
        return t.hashCode() + t.getClass().getName();
    }

    public static <K, V> ExpireTemplate<K, V> accessToTheCacheTemplate(String factoryBeanName) {
        ExpireTemplate<K, V> expireTemplate = null;
        try {
            Object bean = Application.context.getBean(factoryBeanName);
            Assert.notNull(bean,
                    "expireTemplate [" + ExpireTemplate.class.getName() + "] no found in ioc");

            if (bean instanceof ExpireTemplate) {
                expireTemplate = (ExpireTemplate<K, V>) bean;
            }
            Assert.notNull(expireTemplate, "Bean no instanceof ExpireTemplate");
        } catch (Exception e) {
            throw new IllegalArgumentException("accessToTheCacheTemplate error [" + e.getMessage() + "]");
        }
        return expireTemplate;
    }

    @Override
    public void serial() {
        //write
        writeLock.lock();
        try {
            File file = touch0(this.writePath);
            appendLines(Collections.singletonList(this.persistence.toString()), file,
                    StandardCharsets.UTF_8);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean persistenceExist() {
        readLock.lock();
        try {
            return exist0(this.writePath);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setExpirationPersistence(Long duration, TimeUnit timeUnit) {
        Assert.notNull(duration, "Duration no be null");
        Assert.notNull(timeUnit, "TimeUnit no be null");
        Persistence<K, V> p = this.persistence;
        Entry<K, V> entry = p.getEntry();
        //Verify expiration
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + entry.getKey() + "] value [" + entry.getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new file
            del0(this.writePath);
            //Refresh time due to the key value no change don't need to delete the application cache
            entry.refreshOfExpire(duration, timeUnit);
            //Redefine the cache
            of(Entry.of(entry.getKey(), entry.getValue(), duration, timeUnit), p.getFactoryBeanName()).serial();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void resetExpirationPersistence() {
        Persistence<K, V> per = this.persistence;
        //验证是否过期
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + per.getEntry().getKey() + "] value [" +
                        per.getEntry().getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new key value no change don't need to delete the application cache
            del0(this.writePath);
            //To write a cache file
            of(per.getEntry(), per.getFactoryBeanName()).serial();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void replacePersistence(V newValue) {
        Assert.notNull(newValue, "NewValue no be null");
        Persistence<K, V> per = this.persistence;
        Entry<K, V> entry = per.getEntry();
        //Verify expiration
        Assert.isTrue(expireOfCache(),
                "Already expire key [" + per.entry.getKey() + "] value [" + per.entry.getValue() + "]");
        writeLock.lock();
        try {
            //Delete the old file to add a new file
            del0(this.writePath);
            //Delete the cache because the value changes
            CACHE_MAP.remove(rawHash(entry.getKey(), entry.getValue()));
            //To write a cache file
            of(Entry.of(entry.getKey(), newValue, entry.getDuration(), entry.getTimeUnit()),
                    per.getFactoryBeanName()).serial();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removePersistence() {
        Entry<K, V> entry = this.persistence.getEntry();
        writeLock.lock();
        try {
            //Delete the persistent file
            del0(this.writePath);
            //Delete the cache
            CACHE_MAP.remove(rawHash(entry.getKey(), entry.getValue()));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean expireOfCache() {
        //Expiration date after the current time
        return this.persistence.getExpire().isAfter(LocalDateTime.now());
    }

    @Override
    public Entry<K, V> getEntry() {
        return this.persistence.getEntry();
    }

    @Override
    public Persistence<K, V> getPersistence() {
        return this.persistence;
    }

    @Override
    public void deserialize(@NonNull String path) {
        CompletableFuture.runAsync(() -> {
            try {
                deserializeO(path);
            } catch (Throwable e) {
                io.github.zpf1997.expiring.core.Console.logger.error(
                        "Restore cache prepare error : {}", e.getMessage()
                );
            }
        });
    }

    @Override
    public void deserializeO(String path) {
        Assert.notNull(path, "Path no be null");
        Assert.isTrue(isDirectory(path),
                "This path [" + path + "] belong file no a directory");
        List<File> files = loopFiles(path,
                v -> v.isFile() && v.getName().endsWith(PREFIX_BEFORE));
        Assert.notEmpty(files, "This path [" + path + "] no found files");
        //Loop back
        files.forEach(v -> {
            try {
                deserialize0(v);
            } catch (Throwable e) {
                io.github.zpf1997.expiring.core.Console.logger.error(
                        "Restore cache file {} error : {}", v.getName(), e.getMessage()
                );
            }
        });
    }

    @Override
    public void deserialize0(File file) throws PersistenceException {
        Assert.notNull(file, "File no be null");
        InputStream in = null;
        BufferedReader read = null;
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = file.toURI().toURL();
            in = url.openStream();
            read = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = read.readLine()) != null) {
                //@
                // - This form
                // @
                if (AT.equals(line)) {
                    continue;
                }
                buffer.append(line);
            }
        } catch (Throwable e) {
            throw new PersistenceException("Buff read cache error [" + e.getMessage() + "]");
        } finally {
            CloseUtil.closeQuietly(in);
            CloseUtil.closeQuietly(read);
        }
        //Perform follow-up supplement
        deserialize0(buffer);
    }

    @Override
    public void deserialize0(StringBuilder buffer) throws PersistenceException {
        Assert.notNull(buffer, "Buffer no be null");
        String json = buffer.toString();
        //check json
        Assert.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
        //parse json
        Persistence<K, V> persistence;
        try {
            persistence = JSONObject.parseObject(json, new TypeReference<Persistence<K, V>>() {
            });
        } catch (Exception e) {
            throw new PersistenceException("Buffer data [" + json + " ] parse Persistence error " +
                    "[" + e.getMessage() + "]");
        }
        Entry<K, V> entry = persistence.getEntry();
        //Recalculate the hash
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        //No cache in the cache
        ExpireGlobePersistence<K, V> of = CACHE_MAP.computeIfAbsent(rawHash, v -> of(persistence));
        Assert.notNull(of, "ExpireGlobePersistence no be null");
        //record [op bean]
        ExpireTemplate<K, V> template = accessToTheCacheTemplate(persistence.getFactoryBeanName());
        deserialize0(template, persistence, of.getWritePath());
    }

    @Override
    public void deserialize0(ExpireTemplate<K, V> template, Persistence<K, V> persistence, String writePath)
            throws PersistenceException {
        Assert.notNull(template, "ExpireTemplate no be null");
        Assert.notNull(persistence, "Persistence no be null");
        Assert.notNull(writePath, "WritePath no be null");
        LocalDateTime now = LocalDateTime.now();
        //When the time is equal to or judged failure after now in record time
        if (persistence.getExpire() == null || now.isEqual(persistence.getExpire()) ||
                now.isAfter(persistence.getExpire())) {
            //Delete the persistent file
            del0(writePath);
            throw new PersistenceException("File [" + writePath + "] record time [" + persistence.getExpire() +
                    "] before or equals now");
        }
        Entry<K, V> entry = persistence.getEntry();
        //Remaining time is less than the standard not trigger persistence persistence
        template.putVal(entry.getKey(), entry.getValue(),
                condition(now, persistence.getExpire(), entry.getTimeUnit()),
                entry.getTimeUnit());
        //Print successfully restore cached information
        Console.logger.info("Current key {} value {} Has returned to the project of cache",
                entry.getKey(), entry.getKey());
    }

    @Override
    public Long condition(LocalDateTime now, LocalDateTime expire, TimeUnit unit) throws PersistenceException {
        Assert.notNull(now, "Now no be null");
        Assert.notNull(expire, "Expire no be null");
        Assert.notNull(unit, "Unit no be null");
        Duration between = Duration.between(now, expire);
        long value;
        switch (unit) {
            case SECONDS:
                value = between.getSeconds();
                break;
            case MINUTES:
                value = between.get(ChronoUnit.MINUTES);
                break;
            case HOURS:
                value = between.get(ChronoUnit.HOURS);
                break;
            case DAYS:
                value = between.get(ChronoUnit.DAYS);
                break;
            default:
                throw new PersistenceException(
                        "This already supported SECONDS MINUTES HOURS DAYS"
                );
        }
        return value;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    static class Persistence<K, V> implements Serializable {
        private static final long serialVersionUID = 5916681709307714445L;
        private Entry<K, V> entry;
        private String factoryBeanName;
        private LocalDateTime expire;
        static final String FORMAT = AT + "\n" + "%s" + "\n" + AT;

        public Persistence(Entry<K, V> entry) {
            this.entry = entry;
        }

        public static <K, V> Persistence<K, V> of(@NonNull Entry<K, V> entry) {
            Assert.notNull(entry.getKey(), "Key no be null");
            Assert.notNull(entry.getValue(), "Value no be null");
            if (entry.getDuration() == null) {
                entry.setDuration(cacheProperties.getDefaultExpireTime());
            }
            if (entry.getTimeUnit() == null) {
                entry.setTimeUnit(cacheProperties.getDefaultExpireTimeUnit());
            }
            return new Persistence<>(entry);
        }

        public Persistence<K, V> expireOn(String factoryBeanName) {
            Assert.isTrue(StringUtils.isNotBlank(factoryBeanName), "factoryBeanName no be blank");
            K key = this.entry.getKey();
            //get template
            ExpireTemplate<K, V> template = accessToTheCacheTemplate(factoryBeanName);
            Assert.isTrue(template.hasKey(key),
                    "Key [" + this.entry.getKey() + "] no exist in cache");
            //For the rest of the due millisecond value
            Long expectedExpiration = template.getExpectedExpiration(key);
            Assert.isTrue(expectedExpiration != null && expectedExpiration != 0,
                    "Expected Expiration null or be zero");
            //give expire
            this.expire = LocalDateTime.now().plus(expectedExpiration, ChronoUnit.MILLIS);
            this.factoryBeanName = factoryBeanName;
            return this;
        }

        @Override
        public String toString() {
            return String.format(FORMAT, JSON.toJSONString(this));
        }
    }
}
