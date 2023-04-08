package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.core.persistence.Entry;
import io.github.zpf9705.expiring.core.persistence.ExpireByteGlobePersistence;
import io.github.zpf9705.expiring.core.persistence.ExpireSimpleGlobePersistence;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class ExpireBytesPersistenceUtils {

    /**
     * Put {@code key} and {@code value} and {@code duration} and {@code timeUnit} in to persistence
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration can be {@literal null}.
     * @param timeUnit can be  {@literal null}.
     */
    public static void putPersistence(@NonNull byte[] key, @NonNull byte[] value,
                                      @Nullable Long duration,
                                      @Nullable TimeUnit timeUnit) {
        //get current thread factory name
        String factoryName = ExpireFactoryNameHolder.getFactoryName();
        //async
        run(() -> {
            AssertUtils.Persistence.hasText(factoryName, "factoryName no be null");
            ExpireByteGlobePersistence put =
                    ExpireByteGlobePersistence.ofSetBytes(Entry.of(key, value, duration, timeUnit), factoryName);
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
     */
    public static void replaceValuePersistence(@NonNull byte[] key, @NonNull byte[] value,
                                               @NonNull byte[] newValue) {
        run(() -> {
            ExpireByteGlobePersistence replace = ExpireByteGlobePersistence.ofGetBytes(key, value);
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
     */
    public static void replaceDurationPersistence(@NonNull byte[] key, @NonNull byte[] value,
                                                  @NonNull Long duration, @NonNull TimeUnit timeUnit) {
        run(() -> {
            ExpireByteGlobePersistence replaceDuration = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    /**
     * Rest a {@code key} and {@code value} combination of persistence
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    public static void restDurationPersistence(@NonNull byte[] key, @NonNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence reset = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    public static void removePersistence(@NonNull byte[] key, @NonNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param keys must not be {@literal null}.
     */
    public static void removePersistenceWithKeys(@NonNull byte[]... keys) {
        run(() -> {
            for (byte[] key : keys) {
                ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key);
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
     */
    public static void removeSimilarKeyPersistence(@NonNull byte[] key) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            List<ExpireByteGlobePersistence> similar = ExpireByteGlobePersistence.ofGetSimilarBytes(key);
            AssertUtils.Persistence.notEmpty(similar,
                    "No found key [" + Arrays.toString(key) + "] similar persistence");
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
