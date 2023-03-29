package io.github.zpf9705.util;


import io.github.zpf9705.persistence.Entry;
import io.github.zpf9705.persistence.ExpireGlobePersistence;
import io.github.zpf9705.logger.Console;

import java.util.concurrent.TimeUnit;

/**
 * Cache persistence tool
 * <ul>
 *     {@link ExpireGlobePersistence}
 * </ul>
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class ExpirePersistenceUtils {

    /**
     * Key/value pair for persistence will need a static solution
     *
     * @param key             persistence key
     * @param value           persistence value
     * @param duration        persistence key of duration
     * @param timeUnit        persistence key of timeUnit
     * @param factoryBeanName cache template for ioc
     * @param <K>             persistence key data type
     * @param <V>             persistence value data type
     */
    public static <K, V> void putPersistence(K key, V value, Long duration, TimeUnit timeUnit,
                                             String factoryBeanName) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> put =
                    ExpireGlobePersistence.of(Entry.of(key, value, duration, timeUnit), factoryBeanName);
            //判断是否已经写入
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "putPersistence");
    }

    /**
     * Replace the corresponding key value the value of a persistent alternatives
     *
     * @param key      persistence key
     * @param value    persistence value
     * @param newValue persistence replace value
     * @param <K>      persistence key data type
     * @param <V>      persistence value data type
     */
    public static <K, V> void replacePersistence(K key, V value, V newValue) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            AssertUtils.Persistence.notNull(newValue, "newValue no be null");
            ExpireGlobePersistence<K, V> replace = ExpireGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    /**
     * Exist to have persistent files to a custom initialization time
     *
     * @param key      persistence key
     * @param value    persistence value
     * @param duration persistence key of duration
     * @param timeUnit persistence key of timeUnit
     * @param <K>      persistence key data type
     * @param <V>      persistence value data type
     */
    public static <K, V> void setEPersistence(K key, V value, Long duration, TimeUnit timeUnit) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            AssertUtils.Persistence.notNull(duration, "duration no be null");
            AssertUtils.Persistence.notNull(timeUnit, "timeUnit no be null");
            ExpireGlobePersistence<K, V> setE = ExpireGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(setE.persistenceExist(), "persistence no exist");
            setE.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    /**
     * The persistent file already exists for the expiration time limit reset,
     * reset the standard depends on the default system planning
     *
     * @param key   persistence key
     * @param value persistence value
     * @param <K>   persistence key data type
     * @param <V>   persistence value data type
     */
    public static <K, V> void restPersistence(K key, V value) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> reset = ExpireGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    /**
     * After delete system cache value, remove the corresponding persistence file
     *
     * @param key   persistence key
     * @param value persistence value
     * @param <K>   persistence key data type
     * @param <V>   persistence value data type
     */
    public static <K, V> void removePersistence(K key, V value) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> remove = ExpireGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    /**
     * Run the method and capture the exception
     *
     * @param runnable method runnable
     * @param method   method name
     */
    public static void run(Runnable runnable, String method) {
        try {
            runnable.run();
        } catch (Throwable e) {
            Console.exceptionOfDebugOrWare(method, e,
                    "Run the cache Persistence method [{}] An exception occurs [{}]");
        }
    }
}
