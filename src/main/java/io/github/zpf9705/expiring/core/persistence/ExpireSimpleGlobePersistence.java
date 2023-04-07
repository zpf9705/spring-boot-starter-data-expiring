package io.github.zpf9705.expiring.core.persistence;

import ch.qos.logback.core.util.CloseUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.zpf9705.expiring.autoconfigure.Application;
import io.github.zpf9705.expiring.core.ExpireProperties;
import io.github.zpf9705.expiring.core.ExpireTemplate;
import io.github.zpf9705.expiring.core.error.PersistenceException;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.util.AssertUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Global cache persistent objects. Here, a cache object is provided for each key-value pair. When the cache fails,
 * the relevant information will be read from the cache persistent object for subsequent persistent operations
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpireSimpleGlobePersistence<K, V> extends AbstractGlobePersistenceIndicator implements
        GlobePersistence<K, V>, Serializable {

    private static final long serialVersionUID = 7755214068683107950L;

    private static ExpireProperties cacheProperties;

    private static final String PREFIX_BEFORE = ".aof";

    private static final String AT = "@";

    private static final String DEFAULT_WRITE_PATH_SIGN = "default";

    private Persistence<K, V> persistence;

    @Getter
    private String writePath;

    private static final String DEALT = "$*&";

    private static boolean OPEN_PERSISTENCE;

    private static final Map<String, ExpireSimpleGlobePersistence> CACHE_MAP = new ConcurrentHashMap<>();

    public static final ExpireSimpleGlobePersistence<Object, Object> INSTANCE = new ExpireSimpleGlobePersistence<>();

    public static final Map<String, String> KEY_VALUE_HASH = new ConcurrentHashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private ExpireSimpleGlobePersistence() {
    }

    static {
        try {
            resolveCacheProperties();
        } catch (Exception e) {
            Console.error("Load CacheProperties error {}", e.getMessage());
        }
    }

    /**
     * load static cacheProperties
     */
    static void resolveCacheProperties() {
        cacheProperties = Application.context.getBean(ExpireProperties.class);
        OPEN_PERSISTENCE = cacheProperties.getOpenPersistence();
        //if you open persistence will auto create directory
        if (OPEN_PERSISTENCE) {
            checkDirectory();
        }
    }

    /**
     * Check provider persistence path
     */
    static void checkDirectory() {
        String persistencePath = cacheProperties.getPersistencePath();
        if (StringUtils.isNotBlank(persistencePath)) {
            //Determine whether to native folders and whether the folder
            if (!isDirectory(persistencePath)) {
                File directory = mkdir(persistencePath);
                if (!directory.exists()) {
                    checkError(persistencePath);
                }
            }
        }
    }

    public ExpireSimpleGlobePersistence(Persistence<K, V> persistence, String writePath) {
        AssertUtils.Persistence.notNull(writePath, "writePath no be null");
        this.writePath = writePath;
        AssertUtils.Persistence.notNull(persistence, "Persistence no be null !");
        this.persistence = persistence;
    }

    public ExpireSimpleGlobePersistence(Supplier<Persistence<K, V>> persistence, String writePath) {
        AssertUtils.Persistence.notNull(writePath, "writePath no be null");
        this.writePath = writePath;
        Persistence<K, V> p = persistence.get();
        AssertUtils.Persistence.notNull(p, "Persistence no be null !");
        this.persistence = p;
    }

    /**
     * If no found persistence path will check error path and give logger
     *
     * @param persistencePath persistence path
     */
    static void checkError(String persistencePath) {
        String[] pathArray = persistencePath.split("/");
        AssertUtils.Persistence.notEmpty(pathArray,
                "[" + persistencePath + "] no a path");
        String line = "";
        for (String path : pathArray) {
            if (StringUtils.isBlank(path)) {
                continue;
            }
            line += "/" + path;
            AssertUtils.Persistence.isTrue(isDirectory(line),
                    "[" + line + "] no a Directory for your file system");
        }
    }

    /**
     * Through the entry {@link Entry} and container bean name to construct a persistent object
     *
     * @param entry           {@link Entry}
     * @param factoryBeanName factory in container of spring boot bean name
     * @param <K>             key generic
     * @param <V>             value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <K, V> ExpireSimpleGlobePersistence<K, V> of(Entry<K, V> entry, String factoryBeanName) {
        checkOf(entry);
        AssertUtils.Persistence.hasText(factoryBeanName, "FactoryBeanName no be blank");
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        String writePath = rawWritePath(entry.getKey());
        ExpireSimpleGlobePersistence<K, V> persistence = CACHE_MAP.get(rawHash);
        if (persistence == null) {
            synchronized (ExpireSimpleGlobePersistence.class) {
                persistence = CACHE_MAP.get(rawHash);
                if (persistence == null) {
                    CACHE_MAP.putIfAbsent(rawHash,
                            new ExpireSimpleGlobePersistence<>(() -> Persistence.of(entry).expireOn(factoryBeanName)
                                    , writePath));
                    persistence = CACHE_MAP.get(rawHash);
                }
            }
        }
        return persistence;
    }

    /**
     * To build a persistent object by persistent core classes {@link Persistence}
     *
     * @param persistence {@link Persistence}
     * @param <K>         key generic
     * @param <V>         value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <K, V> ExpireSimpleGlobePersistence<K, V> of(Persistence<K, V> persistence) {
        checkPersistence(persistence);
        Entry<K, V> entry = persistence.entry;
        //To recalculate the path
        String writePath = rawWritePath(entry.getKey());
        return new ExpireSimpleGlobePersistence<>(() -> persistence, writePath);
    }

    /**
     * Directly through the key/value pair from the cache take persistent objects in the map
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <K, V> ExpireSimpleGlobePersistence<K, V> of(K key, V value) {
        checkOpenPersistence();
        AssertUtils.Persistence.notNull(key, "Key no be null");
        String persistenceKey;
        if (value == null) {
            String hashKey = rawHash(key);
            String hashValue = KEY_VALUE_HASH.getOrDefault(hashKey, null);
            AssertUtils.Persistence.hasText(hashValue, "Key [" + key + "] no be hash value");
            persistenceKey = rawHashComb(hashKey, hashValue);
        } else {
            persistenceKey = rawHash(key, value);
        }
        ExpireSimpleGlobePersistence<K, V> expireGlobePersistence = CACHE_MAP.get(persistenceKey);
        if (expireGlobePersistence == null) {
            throw new PersistenceException("Key: [" + key + "] raw hash no found persistence");
        }
        return expireGlobePersistence;
    }

    /**
     * Directly through the key from the cache take persistent objects in the map
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <V> value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <K, V> ExpireSimpleGlobePersistence<K, V> of(K key) {
        return of(key, null);
    }

    /**
     * Looking for a similar hash key with persistence
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <V> value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <V, K> List<ExpireSimpleGlobePersistence<K, V>> ofSimilar(K key) {
        checkOpenPersistence();
        AssertUtils.Persistence.notNull(key, "Key no be null");
        List<ExpireSimpleGlobePersistence<K, V>> similar = new ArrayList<>();
        String rawHash = rawHash(key);
        List<String> similarHashKeys = KEY_VALUE_HASH.keySet().stream()
                .filter(v -> v.equals(rawHash) || v.startsWith(rawHash) || v.contains(rawHash) || v.endsWith(rawHash))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(similarHashKeys)) {
            return Collections.emptyList();
        }
        similarHashKeys.forEach(si -> {
            String hashValue = KEY_VALUE_HASH.get(si);
            if (StringUtils.isNotBlank(hashValue)){
                ExpireSimpleGlobePersistence p = CACHE_MAP.get(rawHashComb(si, hashValue));
                if (p != null){
                    similar.add(p);
                }
            }
        });
        return similar;
    }

    /**
     * Check whether accord with standard of cache persistence
     *
     * @param entry {@link Entry}
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkOf(Entry<K, V> entry) {
        checkOpenPersistence();
        checkEntry(entry);
        //empty just pass
        if (entry.getDuration() == null || entry.getTimeUnit() == null) {
            return;
        }
        if ((entry.getDuration() != null && entry.getDuration() >=
                cacheProperties.getNoPersistenceOfExpireTime())
                && (entry.getTimeUnit() != null && entry.getTimeUnit()
                .equals(cacheProperties.getNoPersistenceOfExpireTimeUnit()))) {
            return;
        }
        throw new PersistenceException("Only more than or == " +
                cacheProperties.getNoPersistenceOfExpireTime() + " " +
                cacheProperties.getNoPersistenceOfExpireTimeUnit() +
                " can be persisted so key [" + entry.getKey() + "] value [" + entry.getValue() + "] no persisted ");
    }

    /**
     * Check the data persistence
     *
     * @param persistence Check the item
     * @param <K>         key generic
     * @param <V>         value generic
     */
    static <V, K> void checkPersistence(Persistence<K, V> persistence) {
        AssertUtils.Persistence.notNull(persistence, "Persistence no be null");
        checkEntry(persistence.getEntry());
    }

    /**
     * Check the data entry
     *
     * @param entry Check the item
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkEntry(Entry<K, V> entry) {
        AssertUtils.Persistence.notNull(entry, "Entry no be null");
        AssertUtils.Persistence.notNull(entry.getKey(), "Key no be null");
        AssertUtils.Persistence.notNull(entry.getValue(), "Value no be null");
    }

    /**
     * Check whether the open the persistent cache
     */
    static void checkOpenPersistence() {
        AssertUtils.Persistence.isTrue(OPEN_PERSISTENCE, "No open expiring persistence");
    }

    /**
     * Write the server file path combination
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @return final write path
     */
    protected static <K> String rawWritePath(K key) {
        AssertUtils.Persistence.notNull(key, "Key no be null ");
        return cacheProperties.getPersistencePath()
                //md5 sign to prevent the file name is too long
                + DigestUtil.md5Hex(key.toString())
                + PREFIX_BEFORE;
    }

    /**
     * Combination the hash mark of key/value
     *
     * @param hashKey   must not be {@literal null}.
     * @param hashValue must not be {@literal null}.
     * @return hash mark
     */
    protected static String rawHashComb(String hashKey, String hashValue) {
        return DEALT + hashKey + hashValue;
    }

    /**
     * Calculate the hash mark of key/value
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     * @return hash mark
     */
    protected static <K, V> String rawHash(K key, V value) {
        String rawHashKey = rawHash(key);
        String rawHashValue = rawHash(value);
        KEY_VALUE_HASH.putIfAbsent(rawHashKey, rawHashValue);
        return DEALT + rawHashKey + rawHashValue;
    }

    /**
     * Calculate the hash mark
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return hash mark
     */
    protected static <T> String rawHash(T t) {
        AssertUtils.Persistence.notNull(t, "T when hash no be null");
        return t.hashCode() + t.getClass().getName();
    }

    /**
     * Get a cache template by the bean's name in the container
     *
     * @param factoryBeanName factory in container of spring boot bean name
     * @param <K>             key generic
     * @param <V>             value generic
     * @return {@link ExpireTemplate}
     */
    protected static <K, V> ExpireTemplate<K, V> accessToTheCacheTemplate(String factoryBeanName) {
        ExpireTemplate<K, V> expireTemplate = null;
        try {
            Object bean = Application.context.getBean(factoryBeanName);
            AssertUtils.Persistence.notNull(bean,
                    "ExpireTemplate [" + ExpireTemplate.class.getName() + "] no found in Spring ioc");

            if (bean instanceof ExpireTemplate) {
                expireTemplate = (ExpireTemplate<K, V>) bean;
            }
            AssertUtils.Persistence.notNull(expireTemplate, "Bean no instanceof ExpireTemplate");
        } catch (Exception e) {
            throw new PersistenceException("Access To TheCacheTemplate error [" + e.getMessage() + "]");
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
        AssertUtils.Persistence.notNull(duration, "Duration no be null");
        AssertUtils.Persistence.notNull(timeUnit, "TimeUnit no be null");
        Persistence<K, V> p = this.persistence;
        Entry<K, V> entry = p.getEntry();
        //Verify expiration
        AssertUtils.Persistence.isTrue(expireOfCache(),
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
        AssertUtils.Persistence.isTrue(expireOfCache(),
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
        AssertUtils.Persistence.notNull(newValue, "NewValue no be null");
        Persistence<K, V> per = this.persistence;
        Entry<K, V> entry = per.getEntry();
        //Verify expiration
        AssertUtils.Persistence.isTrue(expireOfCache(),
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
            KEY_VALUE_HASH.remove(rawHash(entry.getKey()));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeAllPersistence() {
        if (CollectionUtils.isEmpty(CACHE_MAP)) {
            return;
        }
        writeLock.lock();
        try {
            for (ExpireSimpleGlobePersistence value : CACHE_MAP.values()) {
                //del persistence
                value.removePersistence();
            }
        } finally {
            CACHE_MAP.clear();
            KEY_VALUE_HASH.clear();
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
    public String getFactoryName() {
        return ExpireSimpleGlobePersistence.class.getName();
    }

    @Override
    public void deserializeWithPath(String path) {
        if (StringUtils.isBlank(path) || Objects.equals(path, DEFAULT_WRITE_PATH_SIGN)) {
            path = cacheProperties.getPersistencePath();
        }
        List<File> files = null;
        if (StringUtils.isBlank(path)) {
            Console.info("Path no be blank , but you provide null");
        } else {
            if (!isDirectory(path)) {
                Console.info("This path [{}] belong file no a directory", path);
            } else {
                files = loopFiles(path, v -> v.isFile() && v.getName().endsWith(PREFIX_BEFORE));
                if (CollectionUtils.isEmpty(files)) {
                    Console.info("This path [{}] no found files", path);
                }
            }
        }
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        //Loop back
        List<File> finalFiles = files;
        CompletableFuture.runAsync(() -> finalFiles.forEach(v -> {
            try {
                deserializeWithFile(v);
            } catch (Throwable e) {
                Console.exceptionOfDebugOrWare(
                        v.getName(), e,
                        "Restore cache file {} error : {}"
                );
            }
        }));
    }

    @Override
    public void deserializeWithFile(File file) {
        AssertUtils.Persistence.notNull(file, "File no be null");
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
        deserializeWithString(buffer);
    }

    @Override
    public void deserializeWithString(StringBuilder buffer) {
        AssertUtils.Persistence.notNull(buffer, "Buffer no be null");
        String json = buffer.toString();
        //check json
        AssertUtils.Persistence.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
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
        ExpireSimpleGlobePersistence<K, V> of = CACHE_MAP.computeIfAbsent(rawHash, v -> of(persistence));
        AssertUtils.Persistence.notNull(of, "ExpireGlobePersistence no be null");
        //record [op bean]
        ExpireTemplate<K, V> template = accessToTheCacheTemplate(persistence.getFactoryBeanName());
        deserializeWithTemplate(template, persistence, of.getWritePath());
    }

    /**
     * Restore memory of expireTemplate regain this info
     *
     * @param template    {@link ExpireTemplate}
     * @param persistence read file of persistence
     * @param writePath   persistence write path
     * @throws PersistenceException Persistence ex
     */
    public void deserializeWithTemplate(ExpireTemplate<K, V> template, Persistence<K, V> persistence, String writePath) {
        AssertUtils.Persistence.notNull(template, "ExpireTemplate no be null");
        AssertUtils.Persistence.notNull(persistence, "Persistence no be null");
        AssertUtils.Persistence.notNull(writePath, "WritePath no be null");
        LocalDateTime now = LocalDateTime.now();
        //When the time is equal to or judged failure after now in record time
        if (persistence.getExpire() == null || now.isEqual(persistence.getExpire()) ||
                now.isAfter(persistence.getExpire())) {
            //Delete the persistent file
            del0(writePath);
            throw new PersistenceException("File [" + writePath + "] record time [" + persistence.getExpire() +
                    "] before or equals now");
        }
        //save key/value with byte[]
        Entry<K, V> entry = persistence.getEntry();
        //serializer key restore
        K key = template.getKeySerializer().deserialize((byte[]) entry.getKey());
        //serializer value restore
        V value = template.getValueSerializer().deserialize((byte[]) entry.getValue());
        //use connection restore byte
        template.getConnectionFactory().getConnection()
                .restoreByteType((byte[]) entry.getKey(), (byte[]) entry.getValue());
        //Remaining time is less than the standard not trigger persistence
        template.opsForValue().set(key, value,
                condition(now, persistence.getExpire(), entry.getTimeUnit()),
                entry.getTimeUnit());
        //Print successfully restore cached information
        Console.info("Cache Key [{}] operation has been restored to the memory", entry.getKey());
    }

    @Override
    protected Long condition(LocalDateTime now, LocalDateTime expire, TimeUnit unit) {
        AssertUtils.Persistence.notNull(now, "Now no be null");
        AssertUtils.Persistence.notNull(expire, "Expire no be null");
        AssertUtils.Persistence.notNull(unit, "Unit no be null");
        long value;
        switch (unit) {
            case SECONDS:
                value = Duration.between(now, expire).getSeconds();
                break;
            case MINUTES:
                value = ChronoUnit.MINUTES.between(now, expire);
                break;
            case HOURS:
                value = ChronoUnit.HOURS.between(now, expire);
                break;
            case DAYS:
                value = ChronoUnit.DAYS.between(now, expire);
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

        public static <K, V> Persistence<K, V> of(Entry<K, V> entry) {
            AssertUtils.Persistence.notNull(entry, "Entry no be null");
            AssertUtils.Persistence.notNull(entry.getKey(), "Key no be null");
            AssertUtils.Persistence.notNull(entry.getValue(), "Value no be null");
            if (entry.getDuration() == null) {
                entry.setDuration(cacheProperties.getDefaultExpireTime());
            }
            if (entry.getTimeUnit() == null) {
                entry.setTimeUnit(cacheProperties.getDefaultExpireTimeUnit());
            }
            return new Persistence<>(entry);
        }

        public Persistence<K, V> expireOn(String factoryBeanName) {
            AssertUtils.Persistence.hasText(factoryBeanName, "factoryBeanName no be blank");
            K key = this.entry.getKey();
            //get template
            ExpireTemplate<K, V> template = accessToTheCacheTemplate(factoryBeanName);
            AssertUtils.Persistence.isTrue(template.exist(key),
                    "Key [" + this.entry.getKey() + "] no exist in cache");
            //For the rest of the due millisecond value
            Long expectedExpiration = template.opsExpirationOperations().getExpectedExpiration(key);
            AssertUtils.Persistence.isTrue(expectedExpiration != null && expectedExpiration != 0,
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
