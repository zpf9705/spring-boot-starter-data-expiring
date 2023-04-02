package io.github.zpf9705.expiring.core;

import org.springframework.lang.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * {@link ExpirationOperations} About some of the key {@code key} expiration time operations
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpirationOperations<K, V> {

    /**
     * Get the key {@code key} of the corresponding cache time : ms
     *
     * @param key must not be {@literal null}.
     * @return unit : ms
     */
    @Nullable
    Long getExpiration(K key);

    /**
     * Get the key {@code key} of the corresponding cache time with specify the unit
     *
     * @param key  must not be {@literal null}.
     * @param unit must not be {@literal null}.
     * @return specify unit
     */
    @Nullable
    Long getExpiration(K key, TimeUnit unit);

    /**
     * Get the key {@code key} of the rest of the expiration time
     *
     * @param key must not be {@literal null}.
     * @return unit : ms
     */
    @Nullable
    Long getExpectedExpiration(K key);

    /**
     * Get the key {@code key} of the rest of the expiration time with specify the unit
     *
     * @param key must not be {@literal null}.
     * @return specify unit
     */
    @Nullable
    Long getExpectedExpiration(K key, TimeUnit unit);

    /**
     * Set this key {@code key} with a new expiration time
     *
     * @param key      must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @return Set result
     */
    Boolean setExpiration(K key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the key {@code key} expiration time with old expiration time
     *
     * @param key must not be {@literal null}.
     * @return Reset result
     */
    Boolean resetExpiration(K key);

    /**
     * To get the operator
     *
     * @return Expire Operations
     */
    ExpireOperations<K, V> getOperations();
}
