package io.github.zpf9705.expiring.core.persistence;

import ch.qos.logback.core.util.CloseUtil;
import cn.hutool.core.exceptions.InvocationTargetRuntimeException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
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
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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

    @Getter
    @Setter
    private static ExpireProperties cacheProperties;

    public static final String PREFIX_BEFORE = ".aof";

    public static final String AT = "@";

    public static final String DEFAULT_WRITE_PATH_SIGN = "default";

    private Persistence<K, V> persistence;

    @Getter
    private String writePath;

    @Setter
    private Class<? extends ExpireSimpleGlobePersistence> globePersistenceClass;

    @Setter
    private Class<? extends Persistence> persistenceClass;

    private static final String DEALT = "$*&";

    private static boolean OPEN_PERSISTENCE;

    public static final Map<String, ExpireSimpleGlobePersistence> CACHE_MAP = new ConcurrentHashMap<>();

    public static final ExpireSimpleGlobePersistence<Object, Object> INSTANCE = new ExpireSimpleGlobePersistence<>();

    public static final Map<String, String> KEY_VALUE_HASH = new ConcurrentHashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

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
        cacheProperties = Application.findBean(ExpireProperties.class);
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

    public ExpireSimpleGlobePersistence() {
        Console.info("Create a new object for ExpireSimpleGlobePersistence , but need initialization");
    }

    public ExpireSimpleGlobePersistence(Persistence<K, V> persistence, String writePath) {
        this.persistence = persistence;
        this.writePath = writePath;
    }

    /**
     * Set in cache map and get an {@code ExpireByteGlobePersistence} with
     * {@code Entry<K,V>} and {@code FactoryBeanName}
     * whether you need to query in the cache
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistenceClass      can be {@literal null}
     * @param entry                 must not be {@literal null}
     * @param factoryBeanName       must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return {@link ExpireByteGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence, K, V> G ofSet
    (@Nullable Class<G> globePersistenceClass, // construct have default value can nullable
     @Nullable Class<P> persistenceClass, // construct have default value can nullable
     @NonNull Entry<K, V> entry,
     @NonNull String factoryBeanName) {
        checkOf(entry);
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        String writePath = rawWritePath(entry.getKey());
        ExpireSimpleGlobePersistence<K, V> persistence = CACHE_MAP.get(rawHash);
        if (persistence == null) {
            synchronized (ExpireSimpleGlobePersistence.class) {
                persistence = CACHE_MAP.get(rawHash);
                if (persistence == null) {
                    persistence =
                            reflectForInstance(globePersistenceClass, persistenceClass, entry, factoryBeanName, writePath);
                    CACHE_MAP.putIfAbsent(rawHash, persistence);
                    /*
                     * CACHE_MAP.putIfAbsent(rawHash,
                     *     new ExpireSimpleGlobePersistence<>(() -> Persistence.of(entry).expireOn(factoryBeanName)
                     *              , writePath));
                     */
                    persistence = CACHE_MAP.get(rawHash);
                }
            }
        }
        /*
         * Must conform to the transformation of idea down
         */
        return (G) persistence;
    }

    /**
     * Use a derived class of this object class object and made a reflection structure subclasses class
     * object instantiation, build a singleton object cache
     * Apply to cache the runtime phase
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistenceClass      can be {@literal null}
     * @param entry                 must not be {@literal null}
     * @param factoryBeanName       must not be {@literal null}
     * @param writePath             must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return Inherit the instantiation singleton
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence, K, V> G reflectForInstance
    (@Nullable Class<G> globePersistenceClass, // construct have default value can nullable
     @Nullable Class<P> persistenceClass, // construct have default value can nullable
     @NonNull Entry<K, V> entry,
     @NonNull String factoryBeanName,
     @NonNull String writePath) {
        G globePersistence;
        try {
            /*
             * @see Persistence#Persistence(Entry)
             */
            P persistence = ReflectUtil.newInstance(persistenceClass, entry);

            /*
             * @see Persistence#expireOn(String)
             * exec add factoryBeanName and expire time
             */
            ReflectUtil.invoke(persistence, Persistence.execute_name, factoryBeanName);

            /*
             * @see ExpireSimpleGlobePersistence#ExpireSimpleGlobePersistence(Persistence, String)
             */
            globePersistence = ReflectUtil.newInstance(globePersistenceClass, persistence, writePath);

            //record class type
            globePersistence.setGlobePersistenceClass(globePersistenceClass);
            globePersistence.setPersistenceClass(persistenceClass);
        } catch (Throwable e) {
            if (e.getMessage().contains("No constructor") || e.getMessage().contains("Instance class")) {
                throw new PersistenceException(
                        "You provider [" + persistenceClass + "] maybe no " +
                                "Construct of Persistence#Persistence(Entry, String) " +
                                "or " +
                                "[" + globePersistenceClass + "] maybe no" +
                                "Construct of ExpireSimpleGlobePersistence#ExpireSimpleGlobePersistence(Persistence, String)" +
                                "if you provider class type is null , please super ExpireSimpleGlobePersistence " +
                                "or ExpireSimpleGlobePersistence.Persistence constructs ," +
                                "now throw msg : " + e.getMessage()
                );
            } else if (e.getMessage().contains("No such method")) {
                throw new PersistenceException(
                        "Please check you provider [" + persistenceClass + " instance Is there a "
                                + Persistence.execute_name + " this method] and args a String , now throw msg : " + e.getMessage()
                );
            } else if (e instanceof InvocationTargetRuntimeException) {
                throw new PersistenceException(
                        "Please check you provider [" + persistenceClass + " instance , is there a method named "
                                + Persistence.execute_name + " exec error , now throw msg : " + e.getMessage()
                );
            }
            throw new UnsupportedOperationException(e.getMessage());
        }
        return globePersistence;
    }

    /**
     * Set in cache map and get an {@code extends ExpireSimpleGlobePersistence} with {@code extends Persistence}
     * And extends ExpireSimpleGlobePersistence class type
     * Don't query is in the cache
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistence           must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return {@link ExpireByteGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, K, V> G ofSetPersistence(
            @NonNull Class<G> globePersistenceClass,
            @NonNull Persistence<K, V> persistence) {
        checkPersistence(persistence);
        Entry<K, V> entry = persistence.entry;
        ExpireSimpleGlobePersistence<K, V> globePersistence =
                CACHE_MAP.computeIfAbsent(rawHash(entry.getKey(), entry.getValue()), v ->
                        reflectForInstance(globePersistenceClass, persistence, rawWritePath(entry.getKey())));
        /*
         * @see Single#just(Object)
         */
        return convert(globePersistence, globePersistenceClass);
    }

    /**
     * Use a derived class of this object class object and made a reflection structure subclasses class
     * object instantiation, build a singleton object cache
     * Apply to restart recovery stage
     *
     * @param globePersistenceClass can be {@literal null}
     * @param persistence           must not be {@literal null}
     * @param writePath             must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @return Inherit the instantiation singleton
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence> G reflectForInstance
    (@NonNull Class<G> globePersistenceClass, // construct have default value can nullable
     @NonNull P persistence,
     @NonNull String writePath) {
        G globePersistence;
        try {
            /*
             * @see ExpireSimpleGlobePersistence#ExpireSimpleGlobePersistence(Persistence, String)
             */
            globePersistence = ReflectUtil.newInstance(
                    globePersistenceClass, persistence, writePath
            );
            globePersistence.setGlobePersistenceClass(globePersistenceClass);
            globePersistence.setPersistenceClass(persistence.getClass());
        } catch (Throwable e) {
            if (e.getMessage().contains("No constructor") || e.getMessage().contains("Instance class")) {
                throw new PersistenceException(
                        "You provider" +
                                "[" + globePersistenceClass + "] maybe no" +
                                "Construct of ExpireSimpleGlobePersistence#ExpireSimpleGlobePersistence(Persistence, String)" +
                                "if you provider class type is null , please super ExpireSimpleGlobePersistence " +
                                "or ExpireSimpleGlobePersistence.Persistence constructs ," +
                                "now throw msg : " + e.getMessage()
                );
            }
            throw new UnsupportedOperationException(e.getMessage());
        }
        return globePersistence;
    }

    /**
     * Get an {@code ExpireSimpleGlobePersistence<K,V>} in cache map with {@code key} and {@code value}
     *
     * @param key                   must not be {@literal null}.
     * @param value                 can be {@literal null}.
     * @param globePersistenceClass can be {@literal null}
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @param <G>                   Inherit generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, K, V> G ofGet(@NonNull K key, @Nullable V value,
                                                                         @Nullable Class<G> globePersistenceClass) {
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
        return convert(expireGlobePersistence, globePersistenceClass);
    }

    /**
     * Get an {@code ExpireSimpleGlobePersistence<K,V>} in cache map with {@code key}
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <V> value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <K, V> ExpireSimpleGlobePersistence<K, V> ofGet(@NonNull K key) {
        return ofGet(key, null, null);
    }

    /**
     * Get any {@code ExpireSimpleGlobePersistence<K,V>} in cache map with similar {@code key}
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <V> value generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <V, K> List<ExpireSimpleGlobePersistence<K, V>> ofGetSimilar(@NonNull K key) {
        checkOpenPersistence();
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
            if (StringUtils.isNotBlank(hashValue)) {
                ExpireSimpleGlobePersistence p = CACHE_MAP.get(rawHashComb(si, hashValue));
                if (p != null) {
                    similar.add(p);
                }
            }
        });
        return similar;
    }

    /**
     * Convert {@code ExpireSimpleGlobePersistence<K,V> simpleGlobePersistence} to provider globePersistenceClass
     *
     * @param expireGlobePersistence must not be {@literal null}
     * @param globePersistenceClass  can be {@literal null}
     * @param <G>                    extend ExpireSimpleGlobePersistence class
     * @param <K>                    key generic
     * @param <V>                    value generic
     * @return extend ExpireSimpleGlobePersistence instance
     */
    public static <G extends ExpireSimpleGlobePersistence, K, V> G convert(@NonNull ExpireSimpleGlobePersistence<K, V>
                                                                                   expireGlobePersistence,
                                                                           //can have no value and default value be this class
                                                                           @Nullable Class<G> globePersistenceClass) {
        try {
            if (globePersistenceClass == null) return (G) expireGlobePersistence;
            return Single.just(expireGlobePersistence).ofType(globePersistenceClass).blockingGet();
        } catch (Throwable e) {
            throw new PersistenceException(
                    "Provider obj no instanceof" +
                            "Can visit the site to check the transformation of knowledge points down ：" +
                            "https://blog.csdn.net/listeningdu/article/details/127944496"
            );
        }
    }

    /**
     * Check whether accord with standard of cache persistence
     *
     * @param entry must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkOf(@NonNull Entry<K, V> entry) {
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
    static <V, K> void checkPersistence(@NonNull Persistence<K, V> persistence) {
        AssertUtils.Persistence.notNull(persistence.getEntry(), "Persistence Entry no be null");
        AssertUtils.Persistence.notNull(persistence.getFactoryBeanName(), "FactoryBeanName no be null");
        AssertUtils.Persistence.notNull(persistence.getExpire(), "Expire no be null");
        checkEntry(persistence.getEntry());
    }

    /**
     * Check the data entry
     *
     * @param entry Check the item
     * @param <K>   key generic
     * @param <V>   value generic
     */
    static <V, K> void checkEntry(@NonNull Entry<K, V> entry) {
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
    static <K> String rawWritePath(@NonNull K key) {
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
    static String rawHashComb(@NonNull String hashKey, @NonNull String hashValue) {
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
    static <K, V> String rawHash(@NonNull K key, @NonNull V value) {
        String rawHashKey = rawHash(key);
        String rawHashValue = KEY_VALUE_HASH.get(rawHashKey);
        if (StringUtils.isBlank(rawHashValue)) {
            rawHashValue = rawHash(value);
            KEY_VALUE_HASH.putIfAbsent(rawHashKey, rawHashValue);
        }
        return DEALT + rawHashKey + rawHashValue;
    }

    /**
     * Calculate the hash mark
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return hash mark
     */
    static <T> String rawHash(@NonNull T t) {
        int hashcode = 0;
        String className = t.getClass().getName();
        if (ArrayUtil.isArray(t)) {
            if (t instanceof byte[]) {
                hashcode = Arrays.hashCode((byte[]) t);
            } else if (t instanceof long[]) {
                hashcode = Arrays.hashCode((long[]) t);
            } else if (t instanceof int[]) {
                hashcode = Arrays.hashCode((int[]) t);
            } else if (t instanceof char[]) {
                hashcode = Arrays.hashCode((char[]) t);
            } else if (t instanceof short[]) {
                hashcode = Arrays.hashCode((short[]) t);
            } else if (t instanceof float[]) {
                hashcode = Arrays.hashCode((float[]) t);
            } else if (t instanceof double[]) {
                hashcode = Arrays.hashCode((double[]) t);
            } else if (t instanceof boolean[]) {
                hashcode = Arrays.hashCode((boolean[]) t);
            } else if (t instanceof Object[]) {
                hashcode = Arrays.hashCode((Object[]) t);
            }
        }
        return hashcode + className;
    }

    /**
     * Get a cache template by the bean's name in the container
     *
     * @param factoryBeanName factory in container of spring boot bean name
     * @return {@link ExpireTemplate}
     */
    public static ExpireTemplate accessToTheCacheTemplate(@NonNull String factoryBeanName) {
        ExpireTemplate expireTemplate = null;
        try {
            Object bean = Application.findBean(factoryBeanName);
            AssertUtils.Persistence.notNull(bean,
                    "ExpireTemplate [" + ExpireTemplate.class.getName() + "] no found in Spring ioc");

            if (bean instanceof ExpireTemplate) {
                expireTemplate = (ExpireTemplate) bean;
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
            ofSet(getGlobePersistenceClass(), getPersistenceClass(), Entry.of(entry.getKey(), entry.getValue(),
                    duration, timeUnit), p.getFactoryBeanName()).serial();
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
            ofSet(getGlobePersistenceClass(), getPersistenceClass(), per.getEntry(),
                    per.getFactoryBeanName()).serial();
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
            ofSet(getGlobePersistenceClass(), getPersistenceClass(),
                    Entry.of(entry.getKey(), newValue, entry.getDuration(), entry.getTimeUnit()),
                    per.getFactoryBeanName()).serial();
        } finally {
            writeLock.unlock();
        }
    }

    public Class<? extends ExpireSimpleGlobePersistence> getGlobePersistenceClass() {
        if (this.globePersistenceClass == null) {
            return ExpireSimpleGlobePersistence.class;
        }
        return this.globePersistenceClass;
    }

    public Class<? extends Persistence> getPersistenceClass() {
        if (this.persistenceClass == null) {
            return Persistence.class;
        }
        return persistenceClass;
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
            CACHE_MAP.clear();
            KEY_VALUE_HASH.clear();
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
    public String getFactoryName() {
        return ExpireSimpleGlobePersistence.class.getName();
    }

    @Override
    public void deserializeWithPath(@Nullable String path) {
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
    public void deserializeWithFile(@NonNull File file) {
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
    public void deserializeWithString(@NonNull StringBuilder buffer) {
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
        //No cache in the cache
        ExpireSimpleGlobePersistence<K, V> of = ofSetPersistence(this.getClass(), persistence);
        AssertUtils.Persistence.notNull(of, "ExpireGlobePersistence no be null");
        //record [op bean]
        ExpireTemplate template = accessToTheCacheTemplate(persistence.getFactoryBeanName());
        deserializeWithTemplate(template, persistence, of.getWritePath());
    }

    /**
     * Using the {@code template } cache data check for the rest of removing, and recovery
     *
     * @param template    must not be {@literal null}
     * @param persistence must not be {@literal null}
     * @param writePath   must not be {@literal null}
     */
    public void deserializeWithTemplate(@NonNull ExpireTemplate template,
                                        @NonNull Persistence<K, V> persistence,
                                        @NonNull String writePath) {
        //current time
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
        //check entry
        checkEntry(entry);
        //Remaining time is less than the standard not trigger persistence
        template.opsForValue().set(entry.getKey(), entry.getValue(),
                condition(now, persistence.getExpire(), entry.getTimeUnit()),
                entry.getTimeUnit());
        //Print successfully restore cached information
        Console.info("Cache Key [{}] operation has been restored to the memory", entry.getKey());
    }

    /**
     * Calculating the cache recovery time remaining with {@code TimeUnit}
     *
     * @param now    must not be {@literal null}
     * @param expire must not be {@literal null}
     * @param unit   must not be {@literal null}
     * @return long result
     */
    public Long condition(@NonNull LocalDateTime now, @NonNull LocalDateTime expire, @NonNull TimeUnit unit) {
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

    @Getter
    @Setter
    public static class Persistence<K, V> implements Serializable {
        private static final long serialVersionUID = 5916681709307714445L;
        private Entry<K, V> entry;
        private String factoryBeanName;
        private LocalDateTime expire;
        static final String FORMAT = AT + "\n" + "%s" + "\n" + AT;
        static final String execute_name = "expireOn";

        public Persistence() {
            Console.warn("Persistence empty after construction, must undertake other variable initialization");
        }

        public Persistence(Entry<K, V> entry) {
            this(entry, null);
        }

        public Persistence(Entry<K, V> entry, String factoryBeanName) {
            this.entry = entry;
            this.factoryBeanName = factoryBeanName;
        }

        public void setFactoryBeanNameAndCheck(String factoryBeanName) {
            if (StringUtils.isBlank(this.factoryBeanName)) {
                if (StringUtils.isNotBlank(factoryBeanName)) {
                    this.factoryBeanName = factoryBeanName;
                }
            }
            AssertUtils.Persistence.hasText(this.factoryBeanName, "Template bean name no be null");
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

        public Persistence<K, V> expireOn(@Nullable String factoryBeanName) {
            setFactoryBeanNameAndCheck(factoryBeanName);
            K key = this.entry.getKey();
            //get template
            ExpireTemplate template = accessToTheCacheTemplate(this.factoryBeanName);
            AssertUtils.Persistence.isTrue(template.exist(key), "Key [" + key + "] no exist in cache");
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
