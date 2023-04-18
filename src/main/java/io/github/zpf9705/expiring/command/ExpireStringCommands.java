package io.github.zpf9705.expiring.command;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.persistence.PersistenceExecTypeEnum;

import java.util.List;
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
    @CanNull
    @PersistenceExec(PersistenceExecTypeEnum.SET)
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
    @PersistenceExec(PersistenceExecTypeEnum.SET)
    Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(PersistenceExecTypeEnum.SET)
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
    @CanNull
    @PersistenceExec(PersistenceExecTypeEnum.SET)
    Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    byte[] get(byte[] key);

    /**
     * Get Similar keys of {@code key}.
     *
     * @param rawKey must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    List<byte[]> getSimilarKeys(byte[] rawKey);

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key      must not be {@literal null}.
     * @param newValue must not be {@literal null}.
     * @return {@literal null}
     */
    @CanNull
    @PersistenceExec(PersistenceExecTypeEnum.REPLACE_VALUE)
    byte[] getAndSet(byte[] key, byte[] newValue);
}
