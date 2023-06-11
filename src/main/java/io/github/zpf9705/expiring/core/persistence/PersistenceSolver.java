package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;

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
    void putPersistence(@NotNull K key, @NotNull V value, @CanNull Long duration, @CanNull TimeUnit timeUnit);

    /**
     * Replace the corresponding {@code  key} {@code value} the value of a {@code newValue}
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     */
    void replaceValuePersistence(@NotNull K key, @NotNull V newValue);

    /**
     * Set a {@code key} and {@code value} with new duration , but if {@code key} exist
     *
     * @param key      must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    void replaceDurationPersistence(@NotNull K key, @NotNull Long duration, @NotNull TimeUnit timeUnit);

    /**
     * Rest a {@code key} and {@code value} combination of persistence
     *
     * @param key must not be {@literal null}.
     */
    void restDurationPersistence(@NotNull K key);

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void removePersistence(@NotNull K key, @NotNull V value);

    /**
     * Remove a {@code key} and {@code value} persistence record
     *
     * @param key must not be {@literal null}.
     */
    void removePersistenceWithKey(@NotNull K key);

    /**
     * Remove a {@code key} Similar  persistence record
     *
     * @param key must not be {@literal null}.
     */
    void removeSimilarKeyPersistence(@NotNull K key);

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
    default void run(@NotNull Runnable runnable, @NotNull String method) {
        MethodRunnableCapable capable = PersistenceRunner.getCapable();
        capable.run(runnable,
                esg -> Console.warn("Run the cache Persistence method [{}] An exception occurs [{}]",
                        method, esg));
    }

    /**
     * @see PersistenceSolver#removeAllPersistence()
     */
    default void delAll() {
        run(ExpireSimpleGlobePersistence.INSTANCE::removeAllPersistence, "removeAllPersistence");
    }
}
