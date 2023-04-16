package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.help.ExpireHelper;
import org.springframework.lang.Nullable;

/**
 * Callback interface for Redis 'low level' code.
 * To be used with {@link ExpireTemplate} execution methods
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
    @Nullable
    T doInExpire(ExpireHelper connection);
}
