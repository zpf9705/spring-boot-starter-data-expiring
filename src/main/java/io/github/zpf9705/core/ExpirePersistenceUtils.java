package io.github.zpf9705.core;

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
public final class ExpirePersistenceUtils {

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

    public static <K, V> void restPersistence(K key, V value) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> reset = ExpireGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

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
            Console.logger.error("expire persistence method [{}] op failed msg [{}]",
                    method, e.getMessage());
        }
    }
}
