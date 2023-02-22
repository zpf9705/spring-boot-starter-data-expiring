package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * <p>
 * Is {@link io.github.zpf9705.core.ExpireAccessor} the implementation class here ,
 * To achieve the some about {@link ExpiringMap} apis ,
 * And to specify the API provides the value of persistence
 * </p>
 *
 * @see io.github.zpf9705.core.ExpireAccessor
 * @see ExpirePersistenceUtils
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
    public V putVal(ExpiringMap<K, V> expiringMap, K key, V value, String factoryBeanName) {
        V put = expiringMap.put(key, value);
        ExpirePersistenceUtils.putPersistence(key, value, null, null, factoryBeanName);
        return put;
    }

    @Override
    public V putValOfDuration(ExpiringMap<K, V> expiringMap, K key, V value, Long duration,
                              TimeUnit timeUnit, String factoryBeanName) {
        V put = expiringMap.put(key, value, duration, timeUnit);
        ExpirePersistenceUtils.putPersistence(key, value, duration, timeUnit, factoryBeanName);
        return put;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Boolean putAll(ExpiringMap<K, V> expiringMap, String factoryBeanName, Entry<K, V>... entries) {
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
    public V getVal(ExpiringMap<K, V> expiringMap, K key) {
        return expiringMap.get(key);
    }

    @Override
    public Long getExpectedExpiration(ExpiringMap<K, V> expiringMap, K key) {
        return expiringMap.getExpectedExpiration(key);
    }

    public Long getExpiration(ExpiringMap<K, V> expiringMap, K key) {
        return expiringMap.getExpiration(key);
    }

    @Override
    public K setExpiration(ExpiringMap<K, V> expiringMap, K key,
                           Long duration, TimeUnit timeUnit) {
        expiringMap.setExpiration(key, duration, timeUnit);
        ExpirePersistenceUtils.setEPersistence(key, getVal(expiringMap, key), duration, timeUnit);
        return key;
    }

    @Override
    public K resetExpiration(ExpiringMap<K, V> expiringMap, K key) {
        expiringMap.resetExpiration(key);
        ExpirePersistenceUtils.restPersistence(key, getVal(expiringMap, key));
        return key;
    }

    @Override
    public Boolean hasKey(ExpiringMap<K, V> expiringMap, K key) {
        return expiringMap.containsKey(key);
    }

    @Override
    public V remove(ExpiringMap<K, V> expiringMap, K key) {
        V removeValue = expiringMap.remove(key);
        ExpirePersistenceUtils.removePersistence(key, removeValue);
        return removeValue;
    }

    @Override
    public Map<K, V> removeType(ExpiringMap<K, V> expiringMap, K key) {
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
                return bKey.startsWith(sKey) ||
                        bKey.endsWith(sKey) ||
                        bKey.contains(sKey);
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
    public V replace(ExpiringMap<K, V> expiringMap, K key, V newValue) {
        V oldValue = expiringMap.replace(key, newValue);
        ExpirePersistenceUtils.replacePersistence(key, oldValue, newValue);
        return oldValue;
    }

    @Override
    public Boolean putIfAbsent(ExpiringMap<K, V> expiringMap, K key, V value,
                               String factoryBeanName) {
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
    public Boolean putIfAbsentOfDuration(ExpiringMap<K, V> expiringMap,
                                         K key, V value,
                                         Long duration, TimeUnit timeUnit,
                                         String factoryBeanName) {
        synchronized (lock) {
            if (hasKey(expiringMap, key)) {
                return false;
            } else {
                putValOfDuration(expiringMap, key, value, duration, timeUnit, factoryBeanName);
                return true;
            }
        }
    }
}
