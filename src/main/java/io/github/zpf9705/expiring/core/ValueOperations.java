package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.CanNull;

import java.util.List;
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
     * There is expiry time for the default configuration
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
    @CanNull
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
    @CanNull
    Boolean setIfAbsent(K key, V value, Long duration, TimeUnit unit);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} get in memory with {@code key}.
     */
    @CanNull
    V get(K key);

    /**
     * Get Similar keys of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null} get keys in memory with {@code key}.
     */
    @CanNull
    List<K> getSimilarKeys(K key);

    /**
     * Set {@code value} of {@code newValue} and return its old value.
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @return if {@literal null} prove first set
     */
    @CanNull
    V getAndSet(K key, V newValue);

    /**
     * To get the operator {@code ExpireOperations}
     *
     * @return Expire Operations
     */
    ExpireOperations<K, V> getOperations();
}
