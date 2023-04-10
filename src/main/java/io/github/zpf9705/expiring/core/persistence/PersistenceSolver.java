package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.logger.Console;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Rear cache persistent action processing interface
 *
 * @author zpf
 * @since 3.0.0
 */
public interface PersistenceSolver<K, V> {

    /**
     * Put {@code key} and {@code value} and {@code duration} and {@code timeUnit} in to persistence
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration can be {@literal null}.
     * @param timeUnit can be  {@literal null}.
     */
    void putPersistence(@NonNull K key, @NonNull V value,
                        @Nullable Long duration,
                        @Nullable TimeUnit timeUnit);

    /**
     * Replace the corresponding {@code  key} {@code value} the value of a {@code newValue}
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     */
    void replaceValuePersistence(@NonNull K key, @NonNull V value, @NonNull V newValue);

    /**
     * Set a {@code key} and {@code value} with new duration , but if {@code key} exist
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    void replaceDurationPersistence(@NonNull K key, @NonNull V value,
                                    @NonNull Long duration, @NonNull TimeUnit timeUnit);

    /**
     * Rest a {@code key} and {@code value} combination of persistence
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void restDurationPersistence(@NonNull K key, @NonNull V value);

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void removePersistence(@NonNull K key, @NonNull V value);

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param keys must not be {@literal null}.
     */
    @SuppressWarnings("unchecked")
    void removePersistenceWithKeys(@NonNull K... keys);

    /**
     * Remove a {@code key} Similar  persistence record
     *
     * @param key must not be {@literal null}.
     */
    void removeSimilarKeyPersistence(@NonNull K key);

    /**
     * Remove all the cache files
     */
    void removeAllPersistence();

    /**
     * Run the method and capture the exception
     *
     * @param runnable method runnable
     * @param method   method name
     */
    default void run(@NonNull Runnable runnable, @NonNull String method) {
        CompletableFuture.runAsync(runnable)
                .whenComplete((v, e) -> Console.exceptionOfDebugOrWare(method, e,
                        "Run the cache Persistence method [{}] An exception occurs [{}]"));
    }
}
