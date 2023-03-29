package io.github.zpf9705.core;

import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.persistence.Entry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Default cheap cache api operator implementation class {@link ValueOperations}
 *
 * @author zpf
 * @since 1.1.0
 */
public class DefaultValueOperations<K, V> extends AbstractOperations<K, V> implements ValueOperations<K, V> {

    public DefaultValueOperations(ExpireTemplate<K, V> expireTemplate) {
        super(expireTemplate);
    }

    public void set(K key, V value) {
        this.syncPut(key, value);
    }

    public void set(K key, V value, Long duration, TimeUnit timeUnit) {
        this.syncPut(key, value, duration, timeUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAll(Entry<K, V>... entries) {
        this.syncSetAll(entries);
    }

    public V get(K key) {
        V o = this.syncGet(key);
        if (Objects.isNull(o) || ArrayUtil.isEmpty(o.getClass().getDeclaredFields())) {
            return null;
        }
        return o;
    }

    public Long getExpectedExpiration(K key) {
        return this.syncGetExpectedExpiration(key);
    }

    public Long getExpiration(K key) {
        return this.syncGetExpiration(key);
    }

    public void setExpiration(K key, Long duration, TimeUnit timeUnit) {
        this.syncPutExpiration(key, duration, timeUnit);
    }

    public void resetExpiration(K key) {
        this.syncResetExpiration(key);
    }

    public Boolean hasKey(K key) {
        return this.syncExist(key);
    }

    public V delete(K key) {
        return this.syncDelete(key);
    }

    @Override
    public Map<K, V> deleteType(K key) {
        return this.syncDeleteType(key);
    }

    @Override
    public Boolean deleteAll() {
        return this.syncDeleteAll();
    }

    public V replace(K key, V value) {
        return this.syncReplace(key, value);
    }

    public Boolean setIfAbsent(K key, V value) {
        return this.putIfAbsent(key, value);
    }

    @Override
    public Boolean setIfAbsent(K key, V value, Long duration, TimeUnit timeUnit) {
        return this.putIfAbsent(key, value, duration, timeUnit);
    }
}
