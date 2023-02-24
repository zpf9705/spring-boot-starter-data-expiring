package io.github.zpf9705.core;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cache tool suggested operation interface
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ValueOperations<K, V> {

    /**
     * To set a memory contains the key and the value of the cache
     * There is time for the default configuration
     *
     * @param key   set a value as a key
     * @param value set a value as a key of value
     */
    void set(K key, V value);

    /**
     * To set a memory contains the key and the value of the cache
     * There is time for you to the custom of the time parameter
     *
     * @param key      set a value as a key
     * @param value    set a value as a key of value
     * @param duration set a duration as a key of expiring time
     * @param timeUnit set a timeUnit as a key of expiring unit
     */
    void set(K key, V value, Long duration, TimeUnit timeUnit);

    /**
     * More than a set key and the value of combination
     *
     * @param entries The key and the value of the combination group {@link Entry}
     */
    @SuppressWarnings("unchecked")
    void setAll(Entry<K, V>... entries);

    /**
     * According to the key to obtain a value value
     *
     * @param key The key to get the value
     * @return Use the key to query to the value of from memory
     */
    V get(K key);

    /**
     * Access to this key the expiration time (unit /ms)
     *
     * @param key Do you want to query time key values
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpiration(K key);

    /**
     * Get the key remaining the expiration time
     *
     * @param key Do you want to query time key values
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpectedExpiration(K key);

    /**
     * For the key value specified expiration time and unit
     *
     * @param key      The specified key
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     */
    void setExpiration(K key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the expiration time, expiration time for the default configuration
     *
     * @param key Do you want to reset the key values
     */
    void resetExpiration(K key);

    /**
     * Determine whether there is the key in the memory
     *
     * @param key The specified key
     * @return Determine the results , If it is true is the existence and vice does not exist
     */
    Boolean hasKey(K key);

    /**
     * To delete a cache for the specified key
     *
     * @param key The specified key
     * @return The return value from a deleted value
     */
    V delete(K key);

    /**
     * Will according to give the key to query the beginning and end ,
     * contains the rules of key/value pair to remove and return the delete key/value pair
     *
     * @param key The specified key
     * @return Be removed in the similar key of key/value pair
     * @since 2.0.0
     */
    Map<K, V> deleteType(K key);

    /**
     * Clear all cache key/value pair
     *
     * @return Determine the results , If the is true the removal of success
     */
    Boolean deleteAll();

    /**
     * Replacement of the specified key value
     *
     * @param key   The specified key
     * @param value The specified key of replace value
     * @return Returns the old value is replaced
     */
    V replace(K key, V value);

    /**
     * In that there is no key/value pair
     *
     * @param key   The specified key
     * @param value The specified value
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean setIfAbsent(K key, V value);

    /**
     * In that there is no key/value pair And set the specified expiration time
     *
     * @param key      The specified key
     * @param value    The specified value
     * @param duration The specified key/value time
     * @param timeUnit The specified key/value time unit
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean setIfAbsent(K key, V value, Long duration, TimeUnit timeUnit);

    /**
     * Get expiring - map operations
     *
     * @return {@link ExpireOperations}
     */
    ExpireOperations<K, V> getOperations();
}
