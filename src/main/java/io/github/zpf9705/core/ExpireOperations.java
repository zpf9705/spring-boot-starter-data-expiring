package io.github.zpf9705.core;


import net.jodah.expiringmap.ExpiringMap;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Template pattern specification interface operation
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpireOperations<K, V> extends Serializable {

    /**
     * Package execution apis and callback {@link Callback}
     *
     * @param action callback way
     * @return execute result
     */
    Object execute(Callback<K, V> action);

    /**
     * put the key/value pair to the memory
     *
     * @param key   The specified key
     * @param value The specified value
     * @return Returns The old value
     */
    V putVal(K key, V value);

    /**
     * To put a memory contains the key and the value of the cache
     * There is time for you to the custom of the time parameter
     *
     * @param key      The specified key
     * @param value    The specified value
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     * @return Returns The old value
     */
    V putVal(K key, V value, Long duration, TimeUnit timeUnit);

    /**
     * More than a set key and the value of combination
     *
     * @param entries The key and the value of the combination group {@link Entry}
     * @return Return true to confirm execution of success and failure
     */
    @SuppressWarnings("unchecked")
    Boolean putAll(Entry<K, V>... entries);

    /**
     * According to the key to obtain a value
     *
     * @param key The specified key
     * @return Use the key to query to the value of from memory
     */
    V getVal(K key);

    /**
     * Access to this key the expiration time (unit /ms)
     *
     * @param key The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpiration(K key);

    /**
     * Get the key remaining the expiration time
     *
     * @param key The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    Long getExpectedExpiration(K key);

    /**
     * For the key value specified expiration time and unit
     *
     * @param key      The specified key
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     * @return Returns set expiration success key
     */
    K setExpiration(K key, Long duration, TimeUnit timeUnit);

    /**
     * Reset the expiration time, expiration time for the default configuration
     *
     * @param key The specified key
     * @return Returns rest success key
     */
    K resetExpiration(K key);

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
    V remove(K key);

    /**
     * Will according to give the key to query the beginning and end ,
     * contains the rules of key/value pair to remove and return the delete key/value pair
     *
     * @param key The specified key
     * @return Be removed in the similar key of key/value pair
     */
    Map<K, V> removeType(K key);

    /**
     * To delete all cache
     *
     * @return returns true or false
     */
    Boolean removeAll();

    /**
     * Replacement of the specified key value
     *
     * @param key The specified key
     * @param value The specified new value
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
    Boolean putIfAbsent(K key, V value);

    /**
     * In that there is no key/value pair And set the specified expiration time
     *
     * @param key      The specified key
     * @param value    The specified value
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    Boolean putIfAbsent(K key, V value, Long duration, TimeUnit timeUnit);

    /**
     * To obtain the key serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<K> getKeySerializer();

    /**
     * To obtain the value serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<V> getValueSerializer();

    /**
     * get an expiringMap
     *
     * @return {@link ExpiringMap}
     */
    ExpiringMap<K, V> opsForExpire();

    /**
     * get a ValueOperations
     *
     * @return {@link ValueOperations}
     */
    ValueOperations<K, V> opsForValue();
}
