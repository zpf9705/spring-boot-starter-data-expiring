package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.ExpireFactoryNameHolder;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Cache persistence tool
 * <ul>
 *     {@link ExpireSimpleGlobePersistence}
 * </ul>
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class ExpirePersistenceUtils {

    /**
     * Put {@code key} and {@code value} and {@code duration} and {@code timeUnit} in to persistence
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @param <K>      key generic
     * @param <V>      value generic
     */
    public static <K, V> void putPersistence(K key, V value, Long duration, TimeUnit timeUnit) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            //get current thread factory name
            String factoryName = ExpireFactoryNameHolder.getFactoryName();
            AssertUtils.Persistence.hasText(factoryName, "factoryName no be null");
            ExpireSimpleGlobePersistence<K, V> put =
                    ExpireSimpleGlobePersistence.of(Entry.of(key, value, duration, timeUnit), factoryName);
            //判断是否已经写入
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
            //after reset current thread Expire Factory Name
            ExpireFactoryNameHolder.restFactoryNamedContent();
        }, "putPersistence");
    }

    /**
     * Replace the corresponding {@code  key} {@code value} the value of a {@code newValue}
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @param <K>      key generic
     * @param <V>      value generic
     */
    public static <K, V> void replacePersistence(K key, V value, V newValue) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            AssertUtils.Persistence.notNull(newValue, "newValue no be null");
            ExpireSimpleGlobePersistence<K, V> replace = ExpireSimpleGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    /**
     * Set a {@code key} and {@code value} with new duration , but if {@code key} exist
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @param <K>      key generic
     * @param <V>      value generic
     */
    public static <K, V> void setEPersistence(K key, V value, Long duration, TimeUnit timeUnit) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            AssertUtils.Persistence.notNull(duration, "duration no be null");
            AssertUtils.Persistence.notNull(timeUnit, "timeUnit no be null");
            ExpireSimpleGlobePersistence<K, V> setE = ExpireSimpleGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(setE.persistenceExist(), "persistence no exist");
            setE.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    /**
     * Rest a {@code key} and {@code value} combination of persistence
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     */
    public static <K, V> void restPersistence(K key, V value) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireSimpleGlobePersistence<K, V> reset = ExpireSimpleGlobePersistence.of(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param <K>   key generic
     * @param <V>   value generic
     */
    public static <K, V> void removePersistence(K key, V value) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            AssertUtils.Persistence.notNull(value, "value no be null");
            ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.of(key, value);
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
    private static void run(Runnable runnable, String method) {
        CompletableFuture.runAsync(runnable)
                .whenComplete((v, e) -> Console.exceptionOfDebugOrWare(method, e,
                        "Run the cache Persistence method [{}] An exception occurs [{}]"));
    }
}
