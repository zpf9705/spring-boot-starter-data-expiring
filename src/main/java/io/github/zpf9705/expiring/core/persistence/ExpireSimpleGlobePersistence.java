package io.github.zpf9705.expiring.core.persistence;

import com.alibaba.fastjson.JSON;
import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.ReloadCarry;
import io.github.zpf9705.expiring.util.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.*;
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
 *      <li>{@link ExpirePersistenceProcessor}</li>
 *  </ul>
 *  And provides a cache persistence mode of operation , Asynchronous and synchronous
 *  <ul>
 *      <li>{@link MethodRunnableCapable#run(Runnable, Consumer)}</li>
 *      <li>{@link PersistenceRunner}</li>
 *  </ul>
 *  Its operation is completely thread-safe, because here is introduced into the read-write lock
 * <p>
 *  {@link #readLock}
 * <p>
 *  {@link #writeLock}
 * <p>
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
 *  The relevant configuration information will be cached in advance {@link #CACHE_MAP}
 *  Attached to the {@link CodecUtils}
 * <p>
 *  {@link #AT}
 * <p>
 *  {@link #PREFIX_BEFORE}
 * <p>
 *  {@link #configuration}
 * <p>
 *  4、When attached project restart automatically read persistence file in memory
 *  <ul>
 *      <li>{@link PersistenceRenewFactory#deserializeWithString(StringBuilder)}</li>
 *      <li>{@link PersistenceRenewFactory#deserializeWithPath(String)}</li>
 *      <li>{@link PersistenceRenewFactory#deserializeWithFile(File)}</li>
 *  </ul>
 *  And provides asynchronous takes up the recovery of the main thread
 * <p>
 *  5、At final , on the analysis of unexpired value for recovery operations
 *  Mainly the {@link ExpireSimpleGlobePersistence} implementation class according to
 *  provide the name of the factory to obtain corresponding overloading interface classes
 *  <ul>
 *      <li>{@link ReloadCarry}</li>
 *      <li>{@link ReloadCarry#reload(Object, Object, Long, TimeUnit)}</li>
 *      <li>{@link ReloadCarry#getReloadCarry(String)}</li>
 *  </ul>
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExpireSimpleGlobePersistence<K, V> extends AbstractPersistenceFileManager implements
        GlobePersistenceWriteProcess<K, V>, PersistenceRenewFactory, Serializable {

    private static final long serialVersionUID = 7755214068683107950L;

    //The system configuration
    private static Configuration configuration;

    //The persistent file suffix
    public static final String PREFIX_BEFORE = ".aof";

    //The file content difference between symbols
    public static final String AT = "@";

    //The default configuration symbol
    public static final String DEFAULT_WRITE_PATH_SIGN = "default";

    //Persistent information model
    private Persistence<K, V> persistence;

    //Implementation type object
    private Class<? extends ExpireSimpleGlobePersistence> globePersistenceClass;

    //Realize persistent information type object
    private Class<? extends Persistence> persistenceClass;

    private static final String DEALT = "$*&";

    //Open the persistent identifier
    private static boolean OPEN_PERSISTENCE;

    //Global information persistent cache
    public static final Map<String, ExpireSimpleGlobePersistence> CACHE_MAP = new ConcurrentHashMap<>();

    //Global removes single object
    public static final ExpireSimpleGlobePersistence<Object, Object> INSTANCE = new ExpireSimpleGlobePersistence<>();

    //Key values of the hash value of the cache object
    public static final Map<String, String> KEY_VALUE_HASH = new ConcurrentHashMap<>();

    //The key  toSting cache object
    public static final Map<String, Object> TO_STRING = new ConcurrentHashMap<>();

    //Read-write lock
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
            checkDirectory(configuration.getPersistencePath());
        }
    }

    public ExpireSimpleGlobePersistence() {
        Console.info("Create a new object for ExpireSimpleGlobePersistence , but need initialization");
    }

    public ExpireSimpleGlobePersistence(Persistence<K, V> persistence, String writePath) {
        this.persistence = persistence;
        setWritePath(writePath);
    }

    /**
     * Set in cache map and get an {@code ExpireByteGlobePersistence} with
     * {@code Entry<K,V>} and {@code FactoryBeanName}
     * whether you need to query in the cache
     *
     * @param globePersistenceClass must not be {@literal null}
     * @param persistenceClass      must not be {@literal null}
     * @param entry                 must not be {@literal null}
     * @param <G>                   Inherit generic
     * @param <P>                   Inherit son generic
     * @param <K>                   key generic
     * @param <V>                   value generic
     * @return {@link ExpireByteGlobePersistence}
     */
    public static <G extends ExpireSimpleGlobePersistence, P extends Persistence, K, V> G ofSet
    (@NotNull Class<G> globePersistenceClass,
     @NotNull Class<P> persistenceClass,
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
     * Apply to cache the runtime phase
     *
     * @param globePersistenceClass must not be {@literal null}
     * @param persistenceClass      must not be {@literal null}
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
    (@NotNull Class<G> globePersistenceClass,
     @NotNull Class<P> persistenceClass,
     @NotNull Entry<K, V> entry,
     @NotNull Long expired,
     @NotNull String writePath) {
        G globePersistence;
        try {
            //instance for persistenceClass
            P persistence = InstanceUtils.instanceWithArgs(persistenceClass, entry);
            //set expired timestamp
            persistence.setExpire(expired);
            //instance for globePersistenceClass
            globePersistence = InstanceUtils.instanceWithArgs(globePersistenceClass, persistence, writePath);
            //record class GlobePersistenceClass type and persistenceClass
            globePersistence.recordCurrentType(globePersistenceClass, persistenceClass);
        } catch (Throwable e) {
            throw new PersistenceException(e.getMessage());
        }
        return globePersistence;
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
            //instance for globePersistenceClass
            globePersistence = InstanceUtils.instanceWithArgs(globePersistenceClass, persistence, writePath);
            //record class GlobePersistenceClass type and persistenceClass
            globePersistence.recordCurrentType(globePersistenceClass, persistence.getClass());
        } catch (Throwable e) {
            throw new PersistenceException(e.getMessage());
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
            String hashKey = CodecUtils.rawHashWithType(key);
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
        String realKey = CodecUtils.toStingBeReal(key);
        List<String> similarElement = CodecUtils.findSimilarElement(TO_STRING.keySet(), realKey);
        if (CollectionUtils.simpleIsEmpty(similarElement)) {
            return Collections.emptyList();
        }
        return similarElement.stream().map(toStringKey -> {
            G g;
            Object keyObj = TO_STRING.get(toStringKey);
            if (keyObj == null) {
                return null;
            }
            String keyHash = CodecUtils.rawHashWithType(keyObj);
            String valueHash = KEY_VALUE_HASH.get(CodecUtils.rawHashWithType(keyObj));
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
            return TypeUtils.convert(expireGlobePersistence, globePersistenceClass);
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
            if (configuration.isDefaultCompareWithExpirePersistence()) {
                return;
            }
            throw new PersistenceException("Default setting no support be persisted");
        }
        if (entry.getTimeUnit().toMillis(entry.getDuration()) >=
                configuration.getDefaultNoPersistenceExpireTimeToMille()) {
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
                + CodecUtils.md5Hex(key)
                + PREFIX_BEFORE;
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
        String rawHashKey = CodecUtils.rawHashWithType(key);
        String rawHashValue = KEY_VALUE_HASH.get(rawHashKey);
        if (StringUtils.simpleIsBlank(rawHashValue)) {
            rawHashValue = CodecUtils.rawHashWithType(value);
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
        TO_STRING.putIfAbsent(CodecUtils.toStingBeReal(key), key);
    }

    /**
     * Record the current implementation of the class object type
     * In order to facilitate the subsequent recovery
     *
     * @param globePersistenceClass {{@link #setGlobePersistenceClass(Class)}}
     * @param persistenceClass      {@link #setPersistenceClass(Class)}
     */
    void recordCurrentType(@NotNull Class<? extends ExpireSimpleGlobePersistence> globePersistenceClass,
                           @NotNull Class<? extends Persistence> persistenceClass) {
        setGlobePersistenceClass(globePersistenceClass);
        setPersistenceClass(persistenceClass);
    }

    /**
     * Set a son for {@code ExpireSimpleGlobePersistence}
     *
     * @param globePersistenceClass {@link ExpireSimpleGlobePersistence}
     */
    public void setGlobePersistenceClass(Class<? extends ExpireSimpleGlobePersistence> globePersistenceClass) {
        this.globePersistenceClass = globePersistenceClass;
    }

    /**
     * Set a son for {@code Persistence}
     *
     * @param persistenceClass {@link Persistence}
     */
    public void setPersistenceClass(Class<? extends Persistence> persistenceClass) {
        this.persistenceClass = persistenceClass;
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
            writeSingleFileLine(this.persistence.toString());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean persistenceExist() {
        readLock.lock();
        try {
            return existCurrentWritePath();
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
            this.delWithCurrentWritePath();
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
            this.delWithCurrentWritePath();
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
            this.delWithCurrentWritePath();
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
            this.delWithCurrentWritePath();
            //Delete the cache
            CACHE_MAP.remove(rawHash(entry.getKey(), entry.getValue()));
            KEY_VALUE_HASH.remove(CodecUtils.rawHashWithType(entry.getKey()));
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
        return this.persistence.getExpireLocalDateTime().isAfter(LocalDateTime.now());
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
    public String getRenewFactoryName() {
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
            AbleUtils.close(in, read);
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
     * @param t   must not be {@literal null}
     * @param <T> extends for {@link ExpireSimpleGlobePersistence}
     */
    public <T extends ExpireSimpleGlobePersistence<K, V>> void deserializeWithEntry(@NotNull T t) {
        //current time
        LocalDateTime now = LocalDateTime.now();
        Persistence<K, V> persistence = t.getPersistence();
        //When the time is equal to or judged failure after now in record time
        if (persistence.getExpire() == null || now.isEqual(persistence.getExpireLocalDateTime()) ||
                now.isAfter(persistence.getExpireLocalDateTime())) {
            //Delete the persistent file
            t.delWithCurrentWritePath();
            throw new PersistenceException("File [" + t.getWritePath() + "] record time [" + persistence.getExpire() +
                    "] before or equals now");
        }
        //save key/value with byte[]
        Entry<K, V> entry = persistence.getEntry();
        //check entry
        checkEntry(entry);
        //reload
        ReloadCarry reloadCarry = ReloadCarry.getReloadCarry(configuration.getChooseClient());
        AssertUtils.Persistence.notNull(reloadCarry,
                "[" + configuration.getChooseClient() + "] no found ReloadCarry");
        reloadCarry.reload(entry.getKey(), entry.getValue(),
                condition(now, persistence.getExpireLocalDateTime(), entry.getTimeUnit()),
                entry.getTimeUnit());
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
    private static <V, K> Long expired(@NotNull Entry<K, V> entry) {
        Long expired;
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
    private static Long plusCurrent(@CanNull Long duration, @CanNull TimeUnit timeUnit) {
        if (duration == null) {
            duration = configuration.getDefaultExpireTime();
        }
        if (timeUnit == null) {
            timeUnit = configuration.getDefaultExpireTimeUnit();
        }
        return System.currentTimeMillis() + timeUnit.toMillis(duration);
    }

    /**
     * The persistent attribute model
     *
     * @param <K> key generic
     * @param <V> value generic
     */
    public static class Persistence<K, V> implements Serializable {
        private static final long serialVersionUID = 5916681709307714445L;
        private Entry<K, V> entry;
        private Long expire;
        static final String FORMAT = AT + "\n" + "%s" + "\n" + AT;

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

        public void setEntry(Entry<K, V> entry) {
            this.entry = entry;
        }

        public Entry<K, V> getEntry() {
            return entry;
        }


        public void setExpire(@NotNull Long expire) {
            this.expire = expire;
        }

        public Long getExpire() {
            return expire;
        }

        public LocalDateTime getExpireLocalDateTime() {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(this.expire), ZoneId.systemDefault());
        }

        @Override
        public String toString() {
            return String.format(FORMAT, JSON.toJSONString(this));
        }
    }
}
