package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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
        Assert.notNull(removeValue, "remove return is null !");
        ExpirePersistenceUtils.removePersistence(key, removeValue);
        return removeValue;
    }

    @Override
    public V replace(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V newValue) {
        V oldValue = expiringMap.replace(key, newValue);
        Assert.notNull(oldValue, "replaced oldValue  no  be null");
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
