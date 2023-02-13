package io.github.zpf9705.core;

import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     This abstract class is mainly described {@link net.jodah.expiringmap.ExpiringMap} - apis
 * </p>
 *
 *
 * @author zpf
 * @since 1.1.0
 **/
@AllArgsConstructor
public abstract class AbstractOperations<K, V> {

    ExpireTemplate<K, V> expireTemplate;

    public void syncPut(K key, V value) {
        expireTemplate.putVal(key, value);
    }

    public void syncPut(K key, V value, Long duration, TimeUnit timeUnit) {
        expireTemplate.putVal(key, value, duration, timeUnit);
    }

    @SuppressWarnings("unchecked")
    public void syncSetAll(Entry<K,V>... entries){
        expireTemplate.putAll(entries);
    }

    public V syncGet(K key) {
        return expireTemplate.getVal(key);
    }

    public Long syncGetExpectedExpiration(K key) {
        return expireTemplate.getExpectedExpiration(key);
    }

    public Long syncGetExpiration(K key) {
        return expireTemplate.getExpiration(key);
    }

    public void syncPutExpiration(K key, Long duration, TimeUnit timeUnit) {
        expireTemplate.setExpiration(key, duration, timeUnit);
    }

    public void syncResetExpiration(K key) {
        expireTemplate.resetExpiration(key);
    }

    public Boolean syncExist(K key) {
        return expireTemplate.hasKey(key);
    }

    public V syncDelete(K key) {
        return expireTemplate.remove(key);
    }

    public Boolean syncDeleteAll() {
        return expireTemplate.removeAll();
    }

    public V syncReplace(K key, V value) {
        return expireTemplate.replace(key, value);
    }

    public Boolean putIfAbsent(K key, V value) {
        return expireTemplate.putIfAbsent(key, value);
    }

    public Boolean putIfAbsent(K key, V value, Long duration, TimeUnit timeUnit) {
        return expireTemplate.putIfAbsent(key, value, duration, timeUnit);
    }

    ExpiringSerializer<?> keySerializer() {
        return expireTemplate.getKeySerializer();
    }

    ExpiringSerializer<?> valueSerializer() {
        return expireTemplate.getValueSerializer();
    }

    public ExpireOperations<K, V> getOperations() {
        return this.expireTemplate;
    }
}
