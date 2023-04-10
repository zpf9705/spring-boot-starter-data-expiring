package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.ExpireFactoryNameHolder;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.checkerframework.checker.units.qual.K;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
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
     * @param duration can be {@literal null}.
     * @param timeUnit can be  {@literal null}.
     * @param <K>      key generic
     * @param <V>      value generic
     */
    public static <K, V> void putPersistence(@NonNull K key, @NonNull V value,
                                             @Nullable Long duration,
                                             @Nullable TimeUnit timeUnit) {
        //get current thread factory name
        String factoryName = ExpireFactoryNameHolder.getFactoryName();
        //async
        run(() -> {
            AssertUtils.Persistence.hasText(factoryName, "factoryName no be null");
            @SuppressWarnings("unchecked")
            ExpireSimpleGlobePersistence<K, V> put =
                    ExpireSimpleGlobePersistence.ofSet(
                            ExpireSimpleGlobePersistence.class,
                            ExpireSimpleGlobePersistence.Persistence.class,
                            Entry.of(key, value, duration, timeUnit), factoryName);
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
    public static <K, V> void replaceValuePersistence(@NonNull K key, @NonNull V value, @NonNull V newValue) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replace = ExpireSimpleGlobePersistence.ofGet(key, value,null);
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
    public static <K, V> void replaceDurationPersistence(@NonNull K key, @NonNull V value,
                                                         @NonNull Long duration, @NonNull TimeUnit timeUnit) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replaceDuration = ExpireSimpleGlobePersistence.ofGet(key, value,null);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
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
    public static <K, V> void restDurationPersistence(@NonNull K key, @NonNull V value) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> reset = ExpireSimpleGlobePersistence.ofGet(key, value,null);
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
    public static <K, V> void removePersistence(@NonNull K key, @NonNull V value) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key, value,null);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param keys must not be {@literal null}.
     * @param <K>  key generic
     * @param <V>  value generic
     */
    @SafeVarargs
    public static <K, V> void removePersistenceWithKeys(@NonNull K... keys) {
        run(() -> {
            for (K key : keys) {
                ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key);
                if (!remove.persistenceExist()) {
                    continue;
                }
                remove.removePersistence();
            }
        }, "removePersistenceWithKeys");
    }

    /**
     * Remove a {@code key} Similar  persistence record
     *
     * @param key must not be {@literal null}.
     * @param <K> key generic
     * @param <V> value generic
     */
    public static <K, V> void removeSimilarKeyPersistence(@NonNull K key) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            List<ExpireSimpleGlobePersistence<K, V>> similar = ExpireSimpleGlobePersistence.ofGetSimilar(key);
            AssertUtils.Persistence.notEmpty(similar, "No found key [" + key + "] similar persistence");
            similar.forEach(s -> {
                if (s.persistenceExist()) {
                    s.removePersistence();
                }
            });
        }, "removePersistence");
    }

    /**
     * Remove all the cache files
     */
    public static void removeAllPersistence() {
        run(ExpireSimpleGlobePersistence.INSTANCE::removeAllPersistence, "removeAllPersistence");
    }

    /**
     * Run the method and capture the exception
     *
     * @param runnable method runnable
     * @param method   method name
     */
    private static void run(@NonNull Runnable runnable, @NonNull String method) {
        CompletableFuture.runAsync(runnable)
                .whenComplete((v, e) -> Console.exceptionOfDebugOrWare(method, e,
                        "Run the cache Persistence method [{}] An exception occurs [{}]"));
    }
}
