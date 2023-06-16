package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Mainly applied to restart will persist the value of the recovery by using the method of the interface
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ReloadCenter<K, V> {

    /**
     * Cache value recovery with {@link io.github.zpf9705.expiring.core.persistence.Entry}
     *
     * @param key      must not be {@literal null}
     * @param value    must not be {@literal null}
     * @param duration must not be {@literal null}
     * @param unit     must not be {@literal null}
     */

    void reload(@NotNull K key, @NotNull V value, @NotNull Long duration, @NotNull TimeUnit unit);


    /**
     * Remove expired keys of auxiliary elements.
     * <dl>
     *     <dt>Contain</dt>
     *     <dt>Persistence</dt>
     * </dl>
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     */
    void cleanSupportingElements(@NotNull K key, @NotNull V value);
}
