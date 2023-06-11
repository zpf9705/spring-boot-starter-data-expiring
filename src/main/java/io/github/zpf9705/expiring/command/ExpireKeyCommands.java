package io.github.zpf9705.expiring.command;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.persistence.PersistenceExecTypeEnum;

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
    @CanNull
    @PersistenceExec(value = PersistenceExecTypeEnum.REMOVE_KEYS,
            expectValue = PersistenceExec.ValueExpectations.LONG_NO_ZERO)
    Long delete(byte[]... keys);

    /**
     * According to the key {@code key}value of similarity to obtain the corresponding keys and delete
     *
     * @param key must not be {@literal null}.
     * @return key/value
     */
    @PersistenceExec(value = PersistenceExecTypeEnum.REMOVE_TYPE,
            expectValue = PersistenceExec.ValueExpectations.NOT_EMPTY)
    Map<byte[], byte[]> deleteType(byte[] key);

    /**
     * clean all cache in memory
     *
     * @return true del all right
     */
    @PersistenceExec(value = PersistenceExecTypeEnum.REMOVE_ALL,
            expectValue = PersistenceExec.ValueExpectations.REALLY)
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
    @CanNull
    Long getExpiration(byte[] key);

    /**
     * Get the key {@code key} of the corresponding cache time with specify the unit
     *
     * @param key  must not be {@literal null}.
     * @param unit must not be {@literal null}.
     * @return specify unit
     */
    @CanNull
    Long getExpiration(byte[] key, TimeUnit unit);

    /**
     * Get the key {@code key} of the rest of the expiration time
     *
     * @param key must not be {@literal null}.
     * @return unit : ms
     */
    @CanNull
    Long getExpectedExpiration(byte[] key);

    /**
     * Get the key {@code key} of the rest of the expiration time with specify the unit
     *
     * @param key  must not be {@literal null}.
     * @param unit must not be {@literal null}.
     * @return specify unit
     */
    @CanNull
    Long getExpectedExpiration(byte[] key, TimeUnit unit);

    /**
     * Set this key {@code key} with a new expiration time
     *
     * @param key      must not be {@literal null}.
     * @param duration must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @return Set result
     */
    @PersistenceExec(value = PersistenceExecTypeEnum.REPLACE_DURATION,
            expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean setExpiration(byte[] key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the key {@code key} expiration time with old expiration time
     *
     * @param key must not be {@literal null}.
     * @return Reset result
     */
    @PersistenceExec(value = PersistenceExecTypeEnum.REST_DURATION,
            expectValue = PersistenceExec.ValueExpectations.REALLY)
    Boolean resetExpiration(byte[] key);
}
