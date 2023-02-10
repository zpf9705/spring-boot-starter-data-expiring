package io.github.zpf1997.expiring.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    Expiring Map operation interface
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public interface ExpireAccessor<K, V> {

    V putVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
             @NonNull String factoryBeanName);

    V putValOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                       @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName);

    Boolean putAll(@NonNull ExpiringMap<K, V> expiringMap, @NonNull String factoryBeanName,
                   @NonNull Entry<K, V>... entries);

    V getVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     Long getExpectedExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     Long getExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     K setExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key,
                              @NonNull Long duration, @NonNull TimeUnit timeUnit);

     K resetExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     Boolean hasKey(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     V remove(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

     V replace(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V newValue);

     Boolean putIfAbsent(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                                  @NonNull String factoryBeanName);

    Boolean putIfAbsentOfDuration(@NonNull ExpiringMap<K, V> expiringMap,
                                  @NonNull K key, @NonNull V value,
                                  @NonNull Long duration, @NonNull TimeUnit timeUnit,
                                  @NonNull String factoryBeanName);
}
