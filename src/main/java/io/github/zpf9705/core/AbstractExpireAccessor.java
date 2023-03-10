package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 *
 * Fluent implementation of  {@link io.github.zpf9705.core.ExpireAccessor}
 *
 * To achieve {@link ExpiringMap} apis , And to specify the API provides the value of persistence
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractExpireAccessor<K, V> implements ExpireAccessor<K, V> {

    /*
     * final obj lock
     * */
    private final Object lock = new Object();

    @Override
    public V putVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                    @NonNull String factoryBeanName) {
        V put = expiringMap.put(key, value);
        ExpirePersistenceUtils.putPersistence(key, value, null, null, factoryBeanName);
        return put;
    }

    @Override
    public V putValOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                              @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName) {
        V put = expiringMap.put(key, value, duration, timeUnit);
        ExpirePersistenceUtils.putPersistence(key, value, duration, timeUnit, factoryBeanName);
        return put;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean putAll(@NonNull ExpiringMap<K, V> expiringMap, @NonNull String factoryBeanName,
                          @NonNull Entry<K, V>... entries) {
        Map<K, V> entryMap = new HashMap<>();
        for (Entry<K, V> entry : entries) {
            if (!entry.ofDuration()) {
                entryMap.put(entry.getKey(), entry.getValue());
            } else {
                putValOfDuration(expiringMap, entry.getKey(), entry.getValue(),
                        entry.getDuration(), entry.getTimeUnit(), factoryBeanName);
            }
        }
        if (!CollectionUtils.isEmpty(entryMap)) {
            expiringMap.putAll(entryMap);
            entryMap.forEach((k, v) ->
                    ExpirePersistenceUtils.putPersistence(k, v, null, null,
                            factoryBeanName));
        }
        return true;
    }

    @Override
    public V getVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        return expiringMap.get(key);
    }

    @Override
    public Long getExpectedExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        return expiringMap.getExpectedExpiration(key);
    }

    public Long getExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        return expiringMap.getExpiration(key);
    }

    @Override
    public K setExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key,
                           @NonNull Long duration, @NonNull TimeUnit timeUnit) {
        expiringMap.setExpiration(key, duration, timeUnit);
        ExpirePersistenceUtils.setEPersistence(key, getVal(expiringMap, key), duration, timeUnit);
        return key;
    }

    @Override
    public K resetExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        expiringMap.resetExpiration(key);
        ExpirePersistenceUtils.restPersistence(key, getVal(expiringMap, key));
        return key;
    }

    @Override
    public Boolean hasKey(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        return expiringMap.containsKey(key);
    }

    @Override
    public V remove(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        V removeValue = expiringMap.remove(key);
        ExpirePersistenceUtils.removePersistence(key, removeValue);
        return removeValue;
    }

    @Override
    public Map<K, V> removeType(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
        Map<K, V> map = new HashMap<>();
        if (hasKey(expiringMap, key)) {
            map.put(key, getVal(expiringMap, key));
            remove(expiringMap, key);
        } else {
            Set<K> keys = expiringMap.keySet();
            final Predicate<K> pk = k -> {
                String bKey = k.toString();
                String sKey = key.toString();
                //start / end / contain
                return bKey.startsWith(sKey) || bKey.endsWith(sKey) || bKey.contains(sKey);
            };
            if (keys.stream().anyMatch(pk)) {
                List<K> oKeys = keys.stream()
                        .filter(pk)
                        .collect(Collectors.toList());
                oKeys.forEach(k -> {
                    map.put(k, getVal(expiringMap, k));
                    remove(expiringMap, k);
                });
            }
        }
        return map;
    }

    @Override
    public V replace(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V newValue) {
        V oldValue = expiringMap.replace(key, newValue);
        ExpirePersistenceUtils.replacePersistence(key, oldValue, newValue);
        return oldValue;
    }

    @Override
    public Boolean putIfAbsent(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                               @NonNull String factoryBeanName) {
        V v = expiringMap.putIfAbsent(key, value);
        //null is put success but no null put failed
        if (v == null) {
            ExpirePersistenceUtils.putPersistence(key, value, null, null,
                    factoryBeanName);
            return true;
        }
        return false;
    }

    @Override
    public Boolean putIfAbsentOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
                                         @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName) {
        synchronized (getLock()) {
            if (hasKey(expiringMap, key)) {
                return false;
            } else {
                putValOfDuration(expiringMap, key, value, duration, timeUnit, factoryBeanName);
                return true;
            }
        }
    }

    @NonNull
    public Object getLock(){
        return this.lock;
    }
}
