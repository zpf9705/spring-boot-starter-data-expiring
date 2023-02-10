package com.github.zpf.expiring.core;

import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Expiring - map persistence tool
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public final class ExpirePersistenceUtils {

    public static <K, V> void putPersistence(K key, V value, Long duration, TimeUnit timeUnit,
                                             String factoryBeanName) {
        run(() -> {
            Assert.notNull(key, "key no be null");
            Assert.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> put =
                    ExpireGlobePersistence.of(Entry.of(key, value, duration, timeUnit), factoryBeanName);
            //判断是否已经写入
            Assert.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "putPersistence");
    }

    public static <K, V> void replacePersistence(K key, V value, V newValue) {
        run(() -> {
            Assert.notNull(key, "key no be null");
            Assert.notNull(value, "value no be null");
            Assert.notNull(newValue, "newValue no be null");
            ExpireGlobePersistence<K, V> replace = ExpireGlobePersistence.of(key, value);
            Assert.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    public static <K, V> void setEPersistence(K key, V value, Long duration, TimeUnit timeUnit) {
        run(() -> {
            Assert.notNull(key, "key no be null");
            Assert.notNull(value, "value no be null");
            Assert.notNull(duration, "duration no be null");
            Assert.notNull(timeUnit, "timeUnit no be null");
            ExpireGlobePersistence<K, V> setE = ExpireGlobePersistence.of(key, value);
            Assert.isTrue(setE.persistenceExist(), "persistence no exist");
            setE.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    public static <K, V> void restPersistence(K key, V value) {
        run(() -> {
            Assert.notNull(key, "key no be null");
            Assert.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> reset = ExpireGlobePersistence.of(key, value);
            Assert.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    public static <K, V> void removePersistence(K key, V value) {
        run(() -> {
            Assert.notNull(key, "key no be null");
            Assert.notNull(value, "value no be null");
            ExpireGlobePersistence<K, V> remove = ExpireGlobePersistence.of(key, value);
            Assert.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    public static void run(Runnable runnable, String method) {
        try {
            runnable.run();
        } catch (Throwable e) {
            Console.logger.error("expire persistence method [{}] op failed msg [{}]", method, e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
