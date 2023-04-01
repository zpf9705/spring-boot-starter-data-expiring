package io.github.zpf9705.expiring.command;

import org.springframework.lang.Nullable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Key-specific commands supported by Expire.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireKeyCommands {

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed.
     */
    @Nullable
    Long delete(byte[]... keys);

    /**
     * According to the key {@code key}value of similarity to obtain the corresponding keys and delete
     *
     * @param key must not be {@literal null}.
     * @return key/value
     */
    Map<byte[], byte[]> deleteType(byte[] key);

    /**
     * clean all cache in memory
     *
     * @return true del all right
     */
    Boolean deleteAll();

    /**
     * Determine if given {@code key} exists.
     *
     * @param key must not be {@literal null}.
     * @return true  Determine exit
     */
    Boolean hasKey(byte[] key);

    /**
     * Get the key {@code key} of the corresponding cache time : ms
     *
     * @param key must not be {@literal null}.
     * @return unit : ms
     */
    @Nullable
    Long getExpiration(byte[] key);

    /**
     * Get the key {@code key} of the corresponding cache time with specify the unit
     *
     * @param key  must not be {@literal null}.
     * @param unit must not be {@literal null}.
     * @return specify unit
     */
    @Nullable
    Long getExpiration(byte[] key, TimeUnit unit);

    /**
     * Get the key {@code key} of the rest of the expiration time
     *
     * @param key must not be {@literal null}.
     * @return unit : ms
     */
    @Nullable
    Long getExpectedExpiration(byte[] key);

    /**
     * Get the key {@code key} of the rest of the expiration time with specify the unit
     *
     * @param key must not be {@literal null}.
     * @return specify unit
     */
    @Nullable
    Long getExpectedExpiration(byte[] key, TimeUnit unit);

    /**
     * Set this key {@code key} with a new expiration time
     *
     * @param key      must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    void setExpiration(byte[] key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the key {@code key} expiration time with old expiration time
     *
     * @param key must not be {@literal null}.
     * @return Reset result
     */
    Boolean resetExpiration(byte[] key);
}
