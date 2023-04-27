package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Mainly applied to restart will persist the value of the recovery by using the method of the interface
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public interface ReloadCarry<K, V> {

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
     * According to the class name for the corresponding operation interface
     *
     * @param client choose client name
     * @return {@link ReloadCarry}
     */
    static ReloadCarry getReloadCarry(@NotNull String client) {
        Map<Class<?>, ReloadCarry> reloadCarryMap = ServiceLoadUtils.load(ReloadCarry.class).withClassMap();
        if (CollectionUtils.simpleNotEmpty(reloadCarryMap)) {
            for (Class<?> clazz : reloadCarryMap.keySet()) {
                Reload reload = clazz.getAnnotation(Reload.class);
                if (reload != null) {
                    if (Objects.equals(reload.client(), client)) {
                        return reloadCarryMap.get(clazz);
                    }
                }
            }
        }
        return null;
    }
}
