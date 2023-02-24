package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Defined here about the packaging {@link ExpiringMap} apis method
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
    @Nullable
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
    @Nullable
    V putValOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value, @NonNull Long duration,
                       @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName);

    /**
     * More than a set key and the value of combination
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @param entries         The key and the value of the combination group {@link Entry}
     * @return Return true to confirm execution of success and failure
     */
    @SuppressWarnings("unchecked")
    Boolean putAll(@NonNull ExpiringMap<K, V> expiringMap, @NonNull String factoryBeanName, @NonNull Entry<K, V>... entries);

    /**
     * According to the key to obtain a value
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Use the key to query to the value of from memory
     */
    @Nullable
    V getVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * Access to this key the expiration time (unit /ms)
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    @Nullable
    Long getExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * Get the key remaining the expiration time
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    @Nullable
    Long getExpectedExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * For the key value specified expiration time and unit
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @param duration    The specified key expire time
     * @param timeUnit    The specified key expire time unit
     * @return Returns set expiration success key
     */
    @Nullable
    K setExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull Long duration, @NonNull TimeUnit timeUnit);

    /**
     * Reset the expiration time, expiration time for the default configuration
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Returns rest success key
     */
    @Nullable
    K resetExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * Determine whether there is the key in the memory
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Determine the results , If it is true is the existence and vice does not exist
     */
    Boolean hasKey(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * To delete a cache for the specified key
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return The return value from a deleted value
     */
    @Nullable
    V remove(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * Will according to give the key to query the beginning and end ,
     * contains the rules of key/value pair to remove and return the delete key/value pair
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @return Be removed in the similar key of key/value pair
     */
    Map<K, V> removeType(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key);

    /**
     * Replacement of the specified key value
     *
     * @param expiringMap {@link ExpiringMap}
     * @param key         The specified key
     * @param newValue    The specified new value
     * @return Returns the old value is replaced
     */
    @Nullable
    V replace(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V newValue);

    /**
     * In that there is no key/value pair
     *
     * @param expiringMap     {@link ExpiringMap}
     * @param key             The specified key
     * @param value           The specified value
     * @param factoryBeanName The name of the cache template defined in the spring container bean
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean putIfAbsent(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                        @NonNull String factoryBeanName);

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
    Boolean putIfAbsentOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                                  @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName);
}
