package io.github.zpf9705.core;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This abstract class is mainly described {@link net.jodah.expiringmap.ExpiringMap} - apis
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
@AllArgsConstructor
public abstract class AbstractOperations<K, V> {

    ExpireTemplate<K, V> expireTemplate;

    /**
     * sync to put key/value
     *
     * @param key   The specified key
     * @param value The specified value
     */
    public void syncPut(K key, V value) {
        expireTemplate.putVal(key, value);
    }

    /**
     * sync to put key/value with time
     *
     * @param key      The specified key
     * @param value    The specified value
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     */
    public void syncPut(K key, V value, Long duration, TimeUnit timeUnit) {
        expireTemplate.putVal(key, value, duration, timeUnit);
    }

    /**
     * sync to put key/value group
     *
     * @param entries key/value group {@link Entry}
     */
    @SuppressWarnings("unchecked")
    public void syncSetAll(Entry<K, V>... entries) {
        expireTemplate.putAll(entries);
    }

    /**
     * sync according to the key to obtain a value
     *
     * @param key The specified key
     * @return current value in memory
     */
    public V syncGet(K key) {
        return expireTemplate.getVal(key);
    }

    /**
     * sync access to this key the expiration time (unit /ms)
     *
     * @param key The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    public Long syncGetExpiration(K key) {
        return expireTemplate.getExpiration(key);
    }

    /**
     * sync get the key remaining the expiration time
     *
     * @param key The specified key
     * @return Returns the corresponding timestamp and util: ms
     */
    public Long syncGetExpectedExpiration(K key) {
        return expireTemplate.getExpectedExpiration(key);
    }

    /**
     * For the key value specified expiration time and unit
     *
     * @param key      The specified key
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     */
    public void syncPutExpiration(K key, Long duration, TimeUnit timeUnit) {
        expireTemplate.setExpiration(key, duration, timeUnit);
    }

    /**
     * Reset the expiration time, expiration time for the default configuration
     *
     * @param key The specified key
     */
    public void syncResetExpiration(K key) {
        expireTemplate.resetExpiration(key);
    }

    /**
     * Determine whether there is the key in the memory
     *
     * @param key The specified key
     * @return Determine the results , If it is true is the existence and vice does not exist
     */
    public Boolean syncExist(K key) {
        return expireTemplate.hasKey(key);
    }

    /**
     * To delete a cache for the specified key
     *
     * @param key The specified key
     * @return The return value from a deleted value
     */
    public V syncDelete(K key) {
        return expireTemplate.remove(key);
    }

    /**
     * Will according to give the key to query the beginning and end ,
     * contains the rules of key/value pair to remove and return the delete key/value pair
     *
     * @param key The specified key
     * @return Be removed in the similar key of key/value pair
     */
    public Map<K, V> syncDeleteType(K key) {
        return expireTemplate.removeType(key);
    }

    /**
     * del all cache
     *
     * @return result true will be success {@link Clearable}
     */
    public Boolean syncDeleteAll() {
        return expireTemplate.removeAll();
    }

    /**
     * Replacement of the specified key value
     *
     * @param key   The specified key
     * @param value The specified new value
     * @return Returns the old value is replaced
     */
    public V syncReplace(K key, V value) {
        return expireTemplate.replace(key, value);
    }

    /**
     * In that there is no key/value pair
     *
     * @param key   The specified key
     * @param value The specified value
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    public Boolean putIfAbsent(K key, V value) {
        return expireTemplate.putIfAbsent(key, value);
    }

    /**
     * In that there is no key/value pair And set the specified expiration time
     *
     * @param key      The specified key
     * @param value    The specified value
     * @param duration The specified key expire time
     * @param timeUnit The specified key expire time unit
     * @return If returns true, then add memory success, or false, indicates the key/value pair already exists
     */
    public Boolean putIfAbsent(K key, V value, Long duration, TimeUnit timeUnit) {
        return expireTemplate.putIfAbsent(key, value, duration, timeUnit);
    }

    /**
     * To obtain the key serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<?> keySerializer() {
        return expireTemplate.getKeySerializer();
    }

    /**
     * To obtain the value serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<?> valueSerializer() {
        return expireTemplate.getValueSerializer();
    }

    /**
     * To get the operator
     *
     * @return {@link ExpireOperations}
     */
    public ExpireOperations<K, V> getOperations() {
        return this.expireTemplate;
    }
}
