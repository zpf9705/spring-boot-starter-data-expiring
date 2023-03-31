package io.github.zpf9705.expiring.core;

import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Expiring operations for simple (or in Expiring terminology 'string' 'object') values.
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ValueOperations<K, V> {

    /**
     * Set {@code value} for {@code key}.
     * There is expire time for the default configuration
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void set(K key, V value);

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     */
    void set(K key, V value, Long duration, TimeUnit unit);

    /**
     * Set {@code key} to hold the string {@code value} if {@code key} is absent.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null} If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    @Nullable
    Boolean setIfAbsent(K key, V value);

    /**
     * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is absent.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     * @return {@literal null} If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    @Nullable
    Boolean setIfAbsent(K key, V value, Long duration, TimeUnit unit);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} get in memory with {@code key} , when used in pipeline / transaction.
     */
    @Nullable
    V get(K key);

    /**
     * Set {@code value} of {@code newValue} and return its old value.
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     */
    @Nullable
    V getAndSet(K key, V newValue);

    /**
     * To get the operator
     *
     * @return Expire Operations
     */
    ExpireOperations<K, V> getOperations();
}
