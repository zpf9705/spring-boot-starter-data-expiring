package com.github.zpf.expiring.core;


import net.jodah.expiringmap.ExpiringMap;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *     Template pattern specification interface operation
 * </p>
 *
 * Here provides the API set about template pattern {@link ExpiringMap}
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpireOperations<K, V> extends Serializable {

    Object execute(Callback<K, V> action);

    V putVal(K key, V value);

    V putVal(K key, V value, Long duration, TimeUnit timeUnit);

    Boolean putAll(Entry<K, V>... entries);

    V getVal(K key);

    Long getExpectedExpiration(K key);

    Long getExpiration(K key);

    K setExpiration(K key, Long duration, TimeUnit timeUnit);

    K resetExpiration(K key);

    Boolean hasKey(K key);

    V remove(K key);

    Boolean removeAll();

    V replace(K key, V value);

    Boolean putIfAbsent(K key, V value);

    Boolean putIfAbsent(K key, V value, Long duration, TimeUnit timeUnit);

    ExpiringSerializer<K> getKeySerializer();

    ExpiringSerializer<V> getValueSerializer();

    ExpiringMap<K, V> opsForExpire();

    ValueOperations<K, V> opsForValue();
}
