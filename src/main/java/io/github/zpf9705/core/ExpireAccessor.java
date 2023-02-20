package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    Defined here about the packaging {@link ExpiringMap} apis method
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface ExpireAccessor<K, V> {

    /**
     * put the key/value pair to the memory
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param key             The specified key
     * @param value           The specified value
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @return Returns The old value
     */
    V putVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value, @NonNull String factoryBeanName);

    /**
     * To put a memory contains the key and the value of the cache
     * There is time for you to the custom of the time parameter
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param key             The specified key
     * @param value           The specified value
     * @param duration        The specified key expire time
     * @param timeUnit        The specified key expire time unit
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @return Returns The old value
     */
    V putValOfDuration(ExpiringMap<K, V> expiringMap, K key, V value, Long duration, TimeUnit timeUnit,
                       String factoryBeanName);

    /**
     * More than a set key and the value of combination
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @param entries         The key and the value of the combination group {@link Entry}
     * @return Return true to confirm execution of success and failure
     */
    @SuppressWarnings("unchecked")
    Boolean putAll(ExpiringMap<K, V> expiringMap, String factoryBeanName, Entry<K, V>... entries);

    /**
     * According to the key to obtain a value
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Use the key to query to the value of from memory
     */
    V getVal(ExpiringMap<K, V> expiringMap, K key);

    /**
     * Access to this key the expiration time (unit /ms)
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpiration(ExpiringMap<K, V> expiringMap, K key);

    /**
     * Get the key remaining the expiration time
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpectedExpiration(ExpiringMap<K, V> expiringMap, K key);

    /**
     * For the key value specified expiration time and unit
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @param duration    The specified key expire time
     * @param timeUnit    The specified key expire time unit
     * @return Returns set expiration success key
     */
    K setExpiration(ExpiringMap<K, V> expiringMap, K key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the expiration time, expiration time for the default configuration
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns rest success key
     */
    K resetExpiration(ExpiringMap<K, V> expiringMap, K key);

    /**
     * Determine whether there is the key in the memory
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Determine the results , If it is true is the existence and vice does not exist
     */
    Boolean hasKey(ExpiringMap<K, V> expiringMap, K key);

    /**
     * To delete a cache for the specified key
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return The return value from a deleted value
     */
    V remove(ExpiringMap<K, V> expiringMap, K key);

    /**
     * Replacement of the specified key value
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @param newValue    The specified new value
     * @return Returns the old value is replaced
     */
    V replace(ExpiringMap<K, V> expiringMap, K key, V newValue);

    /**
     * In that there is no key/value pair
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param key             The specified key
     * @param value           The specified value
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean putIfAbsent(ExpiringMap<K, V> expiringMap, K key, V value, String factoryBeanName);

    /**
     * In that there is no key/value pair And set the specified expiration time
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param key             The specified key
     * @param value           The specified value
     * @param duration        The specified key expire time
     * @param timeUnit        The specified key expire time unit
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean putIfAbsentOfDuration(ExpiringMap<K, V> expiringMap, K key, V value, Long duration, TimeUnit timeUnit,
                                  String factoryBeanName);
}
