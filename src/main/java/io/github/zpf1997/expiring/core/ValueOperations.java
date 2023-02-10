package io.github.zpf1997.expiring.core;

import java.util.concurrent.TimeUnit;

/**
 *
 * Simple using interface for {@link ExpireTemplate}
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ValueOperations<K, V> {

    void set(K key, V value);

    void set(K key, V value, Long duration, TimeUnit timeUnit);

    @SuppressWarnings("unchecked")
    void setAll(Entry<K, V>... entries);

    V get(K key);

    Long getExpectedExpiration(K key);

    Long getExpiration(K key);

    void setExpiration(K key, Long duration, TimeUnit timeUnit);

    void resetExpiration(K key);

    Boolean hasKey(K key);

    V delete(K key);

    Boolean deleteAll();

    V replace(K key, V value);

    Boolean setIfAbsent(K key, V value);

    Boolean setIfAbsent(K key, V value, Long duration, TimeUnit timeUnit);

    ExpireOperations<K, V> getOperations();
}
