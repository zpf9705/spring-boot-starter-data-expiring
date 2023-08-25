package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.help.ExpireHelper;

/**
 * Callback interface for Redis 'low level' code. To be used with {@link ExpireTemplate} execution methods
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireValueCallback<T> {

    /**
     * Gets called by {@link ExpireTemplate} with an active Expire connection. Does not need to care about activating or
     * closing the Helper or handling exceptions.
     *
     * @param connection Expire Helper
     * @return a result object or {@code null} if none
     */
    @CanNull
    T doInExpire(ExpireHelper connection);
}
