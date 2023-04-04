package io.github.zpf9705.expiring.command;

import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * String/Value-specific commands supported by Expire.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireStringCommands {

    /**
     * Set {@code value} for {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null}
     */
    @Nullable
    Boolean set(byte[] key, byte[] value);

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     * @return setE result
     */
    Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null}
     */
    @Nullable
    Boolean setNX(byte[] key, byte[] value);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key      must not be {@literal null}.
     * @param value    must not be {@literal null}.
     * @param duration the key expiration timeout.
     * @param unit     must not be {@literal null}.
     * @return {@literal null}
     */
    @Nullable
    Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null}
     */
    @Nullable
    byte[] get(byte[] key);

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @return {@literal null}
     */
    @Nullable
    byte[] getAndSet(byte[] key, byte[] newValue);
}
