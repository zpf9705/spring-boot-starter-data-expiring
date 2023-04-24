package io.github.zpf9705.expiring.core.persistence;

import ch.qos.logback.core.util.CloseUtil;
import cn.hutool.core.exceptions.InvocationTargetRuntimeException;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import io.github.zpf9705.expiring.core.ExpiringException;
import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.CompatibleUtils;
import io.github.zpf9705.expiring.util.StringUtils;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Helper for the persistent cache And restart the reply , And the only implementation
 * <ul>
 *     <li>{@link ExpireByteGlobePersistence}</li>
 * </ul>
 * Here about key match byte array , Mainly to block out the Java more generic data of differences
 * Here are about cache persistence of the whole process of life
 * He all methods of the realization of all rely on this method, the generic content {@code byte[]}
 * <ul>
 *     <li>{@link GlobePersistenceWriteProcess}</li>
 *     <li>{@link PersistenceRenewFactory}</li>
 * </ul>
 * Briefly describes the implementation process :
 * <p>
 *  1、By manipulating the data into , Proxy objects method execution may step in cache persistence method
 *  <ul>
 *      <li>{@link io.github.zpf9705.expiring.core.ValueOperations}</li>
 *      <li>{@link PersistenceExec}</li>
 *      <li>{@link ExpirePersistenceAfterHandle}</li>
 *  </ul>
 *  And provides a cache persistence mode of operation , Asynchronous and synchronous
 *  <ul>
 *      <li>{@link MethodRunnableCapable#run(Runnable, Consumer)}</li>
 *      <li>{@link PersistenceRunner}</li>
 *  </ul>
 *  Its operation is completely thread-safe, because here is introduced into the read-write lock
 *  {@link #readLock}
 *  {@link #writeLock}
 *  Read not write, write can't read, to ensure the safety of the file system of the thread
 * <p>
 *  2、Will, depending on the type of execution method after different persistence API calls
 *  <ul>
 *      <li>{@link PersistenceSolver}</li>
 *      <li>{@link ExpirePersistenceSolver}</li>
 *      <li>{@link ExpireBytesPersistenceSolver}</li>
 *  </ul>
 * <p>
 *  3、Persistent cache will be stored in the form of a particular file
 *  {@link #AT}
 *  {@link #PREFIX_BEFORE}
 *  {@link #configuration}
 *  4、When attached project restart automatically read persistence file in memory
 *  <ul>
 *      <li>{@link PersistenceRenewFactory#deserializeWithString(StringBuilder)}</li>
 *      <li>{@link PersistenceRenewFactory#deserializeWithPath(String)}</li>
 *      <li>{@link PersistenceRenewFactory#deserializeWithFile(File)}</li>
 *  </ul>
 *  And provides asynchronous takes up the recovery of the main thread
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpireSimpleGlobePersistence<K, V> extends AbstractPersistenceFileManager implements
        GlobePersistenceWriteProcess<K, V>, PersistenceRenewFactory, Serializable {

    private static final long serialVersionUID = 7755214068683107950L;

    private static Configuration configuration;

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

    public static final Map<String, Object> TO_STRING = new ConcurrentHashMap<>();

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
        configuration = Configuration.getConfiguration();
        OPEN_PERSISTENCE = configuration.getOpenPersistence();
        //if you open persistence will auto create directory
        if (OPEN_PERSISTENCE) {
            checkDirectory();
        }
    }

    /**
     * Check provider persistence path
     */
    static void checkDirectory() {
        String persistencePath = configuration.getPersistencePath();
        if (StringUtils.simpleNotBlank(persistencePath)) {
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
            if (StringUtils.simpleIsBlank(path)) {
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
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return {@link ExpireByteGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence, K, V> G ofSet
    (@CanNull Class<G> globePersistenceClass, // construct have default value can nullable
     @CanNull Class<P> persistenceClass, // construct have default value can nullable
     @NotNull Entry<K, V> entry) {
        checkOf(entry);
        String rawHash = rawHash(entry.getKey(), entry.getValue());
        String writePath = rawWritePath(entry.getKey());
        ExpireSimpleGlobePersistence<K, V> persistence = CACHE_MAP.get(rawHash);
        if (persistence == null) {
            synchronized (ExpireSimpleGlobePersistence.class) {
                persistence = CACHE_MAP.get(rawHash);
                if (persistence == null) {
                    persistence =
                            reflectForInstance(globePersistenceClass, persistenceClass, entry, expired(entry),
                                    writePath);
                    CACHE_MAP.putIfAbsent(rawHash, persistence);
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
     * @param expired               must not be {@literal null}
     * @param writePath             must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return Inherit the instantiation singleton
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence, K, V> G reflectForInstance
    (@CanNull Class<G> globePersistenceClass, // construct have default value can nullable
     @CanNull Class<P> persistenceClass, // construct have default value can nullable
     @NotNull Entry<K, V> entry,
     @NotNull LocalDateTime expired,
     @NotNull String writePath) {
        G globePersistence;
        try {
            /*
             * @see Persistence#Persistence(Entry)
             */
            P persistence = ReflectUtil.newInstance(persistenceClass, entry);

            /*
             * @see Persistence#expireOn(LocalDateTime)
             * exec add expire time
             */
            ReflectUtil.invoke(persistence, Persistence.execute_name, expired);

            /*
             * @see ExpireSimpleGlobePersistence#ExpireSimpleGlobePersistence(Persistence, String)
             */
            globePersistence = ReflectUtil.newInstance(globePersistenceClass, persistence, writePath);

            //record class type
            globePersistence.setGlobePersistenceClass(globePersistenceClass);
            globePersistence.setPersistenceClass(persistenceClass);
        } catch (Throwable e) {
            if (e instanceof ExpiringException) {
                throw e;
            }
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
            @NotNull Class<G> globePersistenceClass,
            @NotNull Persistence<K, V> persistence) {
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
    (@NotNull Class<G> globePersistenceClass, // construct have default value can nullable
     @NotNull P persistence,
     @NotNull String writePath) {
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
    public static <G extends ExpireSimpleGlobePersistence, K, V> G ofGet(@NotNull K key, @CanNull V value,
                                                                         @CanNull Class<G> globePersistenceClass) {
        checkOpenPersistence();
        AssertUtils.Persistence.notNull(key, "Key no be null");
        String persistenceKey;
        if (value == null) {
            String hashKey = CompatibleUtils.rawHashWithType(key);
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
     * @param <G> Inherit generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, K> G ofGet(@NotNull K key) {
        return ofGet(key, null, null);
    }

    /**
     * Get any {@code ExpireSimpleGlobePersistence<K,V>} in cache map with similar {@code key}
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <G> Inherit generic
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, K> List<G> ofGetSimilar(@NotNull K key) {
        checkOpenPersistence();
        String realKey = CompatibleUtils.toStingBeReal(key);
        List<String> similarElement = CompatibleUtils.findSimilarElement(TO_STRING.keySet(), realKey);
        if (CollectionUtils.simpleIsEmpty(similarElement)) {
            return Collections.emptyList();
        }
        return similarElement.stream().map(toStringKey -> {
            G g;
            Object keyObj = TO_STRING.get(toStringKey);
            String keyHash = CompatibleUtils.rawHashWithType(keyObj);
            String valueHash = KEY_VALUE_HASH.get(CompatibleUtils.rawHashWithType(keyObj));
            if (StringUtils.simpleNotBlank(valueHash)) {
                g = (G) CACHE_MAP.get(rawHashComb(keyHash, valueHash));
            } else {
                g = null;
            }
            return g;
        }).filter(Objects::nonNull).collect(Collectors.toList());
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
    public static <G extends ExpireSimpleGlobePersistence, K, V> G convert(@NotNull ExpireSimpleGlobePersistence<K, V>
                                                                                   expireGlobePersistence,
                                                                           //can have no value and default value be this class
                                                                           @CanNull Class<G> globePersistenceClass) {
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
    static <V, K> void checkOf(@NotNull Entry<K, V> entry) {
        checkOpenPersistence();
        checkEntry(entry);
        //empty just pass
        if (entry.getDuration() == null || entry.getTimeUnit() == null) {
            return;
        }
        if ((entry.getDuration() != null && entry.getDuration() >=
                configuration.getNoPersistenceOfExpireTime())
                && (entry.getTimeUnit() != null && entry.getTimeUnit()
                .equals(configuration.getNoPersistenceOfExpireTimeUnit()))) {
            return;
        }
        throw new PersistenceException("Only more than or == " +
                configuration.getNoPersistenceOfExpireTime() + " " +
                configuration.getNoPersistenceOfExpireTimeUnit() +
                " can be persisted so key [" + entry.getKey() + "] value [" + entry.getValue() + "] no persisted ");
    }

    /**
     * Check the data persistence
     *
     * @param persistence Check the item
     * @param <K>         key generic
     * @param <V>         value generic
     */
    static <V, K> void checkPersistence(@NotNull Persistence<K, V> persistence) {
        AssertUtils.Persistence.notNull(persistence.getEntry(), "Persistence Entry no be null");
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
    static <V, K> void checkEntry(@NotNull Entry<K, V> entry) {
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
    static <K> String rawWritePath(@NotNull K key) {
        AssertUtils.Persistence.notNull(key, "Key no be null ");
        return configuration.getPersistencePath()
                //md5 sign to prevent the file name is too long
                + DigestUtils.md5Hex(getContentBytes(key))
                + PREFIX_BEFORE;
    }

    /**
     * Get the MD5 encryption byte array
     *
     * @param key must not be {@literal null}
     * @param <K> key generic
     * @return byte array
     */
    static <K> byte[] getContentBytes(@NotNull K key) {
        if (key instanceof byte[]) {
            return (byte[]) key;
        }
        return key.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Combination the hash mark of key/value
     *
     * @param hashKey   must not be {@literal null}.
     * @param hashValue must not be {@literal null}.
     * @return hash mark
     */
    static String rawHashComb(@NotNull String hashKey, @NotNull String hashValue) {
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
    static <K, V> String rawHash(@NotNull K key, @NotNull V value) {
        String rawHashKey = CompatibleUtils.rawHashWithType(key);
        String rawHashValue = KEY_VALUE_HASH.get(rawHashKey);
        if (StringUtils.simpleIsBlank(rawHashValue)) {
            rawHashValue = CompatibleUtils.rawHashWithType(value);
            KEY_VALUE_HASH.putIfAbsent(rawHashKey, rawHashValue);
            recordContentToKeyString(key);
        }
        return DEALT + rawHashKey + rawHashValue;
    }

    /**
     * Converts the String mark of key
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     */
    static <K> void recordContentToKeyString(@NotNull K key) {
        TO_STRING.putIfAbsent(CompatibleUtils.toStingBeReal(key), key);
    }

    /**
     * Get or default with ExpireSimpleGlobePersistence
     *
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public Class<? extends ExpireSimpleGlobePersistence> getGlobePersistenceClass() {
        if (this.globePersistenceClass == null) {
            return ExpireSimpleGlobePersistence.class;
        }
        return this.globePersistenceClass;
    }

    /**
     * Get or default with Persistence
     *
     * @return {@link Persistence}
     */
    public Class<? extends Persistence> getPersistenceClass() {
        if (this.persistenceClass == null) {
            return Persistence.class;
        }
        return persistenceClass;
    }

    @Override
    public void serial() {
        //write
        writeLock.lock();
        try {
            File file = touch0(this.writePath);
            if (file != null) {
                writeSingleFileLine(this.persistence.toString(), file);
            }
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
                    duration, timeUnit)).serial();
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
            ofSet(getGlobePersistenceClass(), getPersistenceClass(), per.getEntry()).serial();
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
                    Entry.of(entry.getKey(), newValue, entry.getDuration(), entry.getTimeUnit())).serial();
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
            KEY_VALUE_HASH.remove(CompatibleUtils.rawHashWithType(entry.getKey()));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeAllPersistence() {
        if (CollectionUtils.simpleIsEmpty(CACHE_MAP)) {
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
    public void deserializeWithPath(@CanNull String path) {
        if (StringUtils.simpleIsBlank(path) || Objects.equals(path, DEFAULT_WRITE_PATH_SIGN)) {
            path = configuration.getPersistencePath();
        }
        List<File> files = null;
        if (StringUtils.simpleIsBlank(path)) {
            Console.info("Path no be blank , but you provide null");
        } else {
            if (!isDirectory(path)) {
                Console.info("This path [{}] belong file no a directory", path);
            } else {
                files = loopFiles(path, v -> v.isFile() && v.getName().endsWith(PREFIX_BEFORE));
                if (CollectionUtils.simpleIsEmpty(files)) {
                    Console.info("This path [{}] no found files", path);
                }
            }
        }
        if (CollectionUtils.simpleIsEmpty(files)) {
            return;
        }
        //Loop back
        List<File> finalFiles = files;
        CompletableFuture.runAsync(() -> finalFiles.forEach(v -> {
            try {
                this.deserializeWithFile(v);
            } catch (Throwable e) {
                Console.warn("Restore cache file {}  error : {}", v.getName(), e.getMessage());
            }
        }));
    }

    @Override
    public void deserializeWithFile(@NotNull File file) {
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
        this.deserializeWithString(buffer);
    }

    @Override
    public void deserializeWithString(@NotNull StringBuilder buffer) {
        String json = buffer.toString();
        //check json
        AssertUtils.Persistence.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
    }

    /**
     * Using the {@code template } cache data check for the rest of removing, and recovery
     *
     * @param persistence must not be {@literal null}
     * @param writePath   must not be {@literal null}
     */
    public void deserializeWithEntry(@NotNull Persistence<K, V> persistence,
                                     @NotNull String writePath) {
    }

    /**
     * Calculating the cache recovery time remaining with {@code TimeUnit}
     *
     * @param now    must not be {@literal null}
     * @param expire must not be {@literal null}
     * @param unit   must not be {@literal null}
     * @return long result
     */
    public Long condition(@NotNull LocalDateTime now, @NotNull LocalDateTime expire, @NotNull TimeUnit unit) {
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

    /**
     * Calculate the expiration time
     *
     * @param entry must not be {@literal null}
     * @param <K>   key generic
     * @param <V>   value generic
     * @return result
     */
    private static <V, K> LocalDateTime expired(@NotNull Entry<K, V> entry) {
        LocalDateTime expired;
        if (entry.haveDuration()) {
            expired = plusCurrent(entry.getDuration(), entry.getTimeUnit());
        } else {
            expired = plusCurrent(null, null);
        }
        return expired;
    }

    /**
     * Calculate the expiration time with {@code plus}
     *
     * @param duration must not be {@literal null}
     * @param timeUnit must not be {@literal null}
     * @return result
     */
    private static LocalDateTime plusCurrent(@CanNull Long duration, @CanNull TimeUnit timeUnit) {
        if (duration == null) {
            duration = configuration.getDefaultExpireTime();
        }
        if (timeUnit == null) {
            timeUnit = configuration.getDefaultExpireTimeUnit();
        }
        ChronoUnit unit;
        switch (timeUnit) {
            case SECONDS:
                unit = ChronoUnit.SECONDS;
                break;
            case MINUTES:
                unit = ChronoUnit.MINUTES;
                break;
            case HOURS:
                unit = ChronoUnit.HOURS;
                break;
            case DAYS:
                unit = ChronoUnit.DAYS;
                break;
            default:
                unit = null;
                break;
        }
        if (unit == null) throw new UnsupportedOperationException(timeUnit.name());
        return LocalDateTime.now().plus(duration, unit);
    }

    @Getter
    @Setter
    public static class Persistence<K, V> implements Serializable {
        private static final long serialVersionUID = 5916681709307714445L;
        private Entry<K, V> entry;
        private LocalDateTime expire;
        static final String FORMAT = AT + "\n" + "%s" + "\n" + AT;
        static final String execute_name = "expireOn";

        public Persistence() {
            Console.warn("Persistence empty after construction, must undertake other variable initialization");
        }

        public Persistence(Entry<K, V> entry) {
            this.entry = entry;
        }

        public static <K, V> Persistence<K, V> of(Entry<K, V> entry) {
            AssertUtils.Persistence.notNull(entry, "Entry no be null");
            AssertUtils.Persistence.notNull(entry.getKey(), "Key no be null");
            AssertUtils.Persistence.notNull(entry.getValue(), "Value no be null");
            if (entry.getDuration() == null) {
                entry.setDuration(configuration.getDefaultExpireTime());
            }
            if (entry.getTimeUnit() == null) {
                entry.setTimeUnit(configuration.getDefaultExpireTimeUnit());
            }
            return new Persistence<>(entry);
        }

        public Persistence<K, V> expireOn(@NotNull LocalDateTime expire) {
            this.expire = expire;
            return this;
        }

        @Override
        public String toString() {
            return String.format(FORMAT, JSON.toJSONString(this));
        }
    }
}
