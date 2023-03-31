package io.github.zpf9705.expiring.connection.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapKeyCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapStringCommands;
import io.github.zpf9705.expiring.connection.AbstractExpireConnection;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@code ExpireConnection} implementation of expire_map.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapConnection extends AbstractExpireConnection {

    private final ExpiringMap<byte[], byte[]> expiringMap;

    public ExpireMapConnection(ExpiringMap<byte[], byte[]> expiringMap) {
        this.expiringMap = expiringMap;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#stringCommands()
     */
    @Override
    public ExpireStringCommands stringCommands() {
        return new ExpireMapStringCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#keyCommands()
     */
    @Override
    public ExpireKeyCommands keyCommands() {
        return new ExpireMapKeyCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     */
    public Boolean put(byte[] key, byte[] value) {
        this.expiringMap.put(key, value);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    public Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        expiringMap.put(key, value, duration, unit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    public Boolean putIfAbsent(byte[] key, byte[] value) {
        byte[] bytes = this.expiringMap.putIfAbsent(key, value);
        return bytes == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        if (Boolean.TRUE.equals(this.setNX(key, value))) {
            this.setExpiration(key, duration, unit);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    public byte[] getVal(byte[] key) {
        return this.expiringMap.get(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    public byte[] replace(byte[] key, byte[] newValue) {
        return this.expiringMap.replace(key, newValue);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @Nullable
    public Long deleteReturnSuccessNum(byte[]... keys) {
        long count = 0L;
        for (byte[] key : keys) {
            if (this.expiringMap.remove(key) != null) {
                count++;
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object, Object)
     */
    public Map<byte[], byte[]> deleteSimilarKey(byte[] key) {
        if (!hasKey(key)) {
            return Collections.emptyMap();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    public Boolean reboot() {
        this.closeThis();
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    public Boolean containsKey(byte[] key) {
        return this.expiringMap.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    public Boolean containsValue(byte[] key) {
        return this.expiringMap.containsValue(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    public Long getExpirationWithKey(byte[] key) {
        return this.expiringMap.getExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    public Long getExpirationWithDuration(byte[] key, TimeUnit unit) {
        Long expiration = this.getExpiration(key);
        if (expiration == null) {
            return null;
        }
        return TimeUnit.MICROSECONDS.convert(expiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    public Long getExpectedExpirationWithKey(byte[] key) {
        return this.expiringMap.getExpectedExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    public Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit) {
        Long expectedExpiration = this.getExpectedExpiration(key);
        if (expectedExpiration == null) {
            return null;
        }
        return TimeUnit.MICROSECONDS.convert(expectedExpiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    public void setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit) {
        this.expiringMap.setExpiration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    public Boolean resetExpirationWithKey(byte[] key) {
        this.expiringMap.resetExpiration(key);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    public void closeThis() {
        this.expiringMap.clear();
    }

    //    /*
//     * final obj lock
//     * */
//    private final Object lock = new Object();
//
//    @Override
//    public V putVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
//                    @NonNull String factoryBeanName) {
//        V put = expiringMap.put(key, value);
//        ExpirePersistenceUtils.putPersistence(key, value, null, null, factoryBeanName);
//        return put;
//    }
//
//    @Override
//    public V putValOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
//                              @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName) {
//        V put = expiringMap.put(key, value, duration, timeUnit);
//        ExpirePersistenceUtils.putPersistence(key, value, duration, timeUnit, factoryBeanName);
//        return put;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public Boolean putAll(@NonNull ExpiringMap<K, V> expiringMap, @NonNull String factoryBeanName,
//                          @NonNull Entry<K, V>... entries) {
//        Map<K, V> entryMap = new HashMap<>();
//        for (Entry<K, V> entry : entries) {
//            if (!entry.ofDuration()) {
//                entryMap.put(entry.getKey(), entry.getValue());
//            } else {
//                putValOfDuration(expiringMap, entry.getKey(), entry.getValue(),
//                        entry.getDuration(), entry.getTimeUnit(), factoryBeanName);
//            }
//        }
//        if (!CollectionUtils.isEmpty(entryMap)) {
//            expiringMap.putAll(entryMap);
//            entryMap.forEach((k, v) ->
//                    ExpirePersistenceUtils.putPersistence(k, v, null, null,
//                            factoryBeanName));
//        }
//        return true;
//    }
//
//    @Override
//    public V getVal(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        return expiringMap.get(key);
//    }
//
//    @Override
//    public Long getExpectedExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        return expiringMap.getExpectedExpiration(key);
//    }
//
//    public Long getExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        return expiringMap.getExpiration(key);
//    }
//
//    @Override
//    public K setExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key,
//                           @NonNull Long duration, @NonNull TimeUnit timeUnit) {
//        expiringMap.setExpiration(key, duration, timeUnit);
//        ExpirePersistenceUtils.setEPersistence(key, getVal(expiringMap, key), duration, timeUnit);
//        return key;
//    }
//
//    @Override
//    public K resetExpiration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        expiringMap.resetExpiration(key);
//        ExpirePersistenceUtils.restPersistence(key, getVal(expiringMap, key));
//        return key;
//    }
//
//    @Override
//    public Boolean hasKey(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        return expiringMap.containsKey(key);
//    }
//
//    @Override
//    public V remove(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        V removeValue = expiringMap.remove(key);
//        ExpirePersistenceUtils.removePersistence(key, removeValue);
//        return removeValue;
//    }
//
//    @Override
//    public Map<K, V> removeType(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key) {
//        Map<K, V> map = new HashMap<>();
//        if (hasKey(expiringMap, key)) {
//            map.put(key, getVal(expiringMap, key));
//            remove(expiringMap, key);
//        } else {
//            Set<K> keys = expiringMap.keySet();
//            final Predicate<K> pk = k -> {
//                String bKey = k.toString();
//                String sKey = key.toString();
//                //start / end / contain
//                return bKey.startsWith(sKey) || bKey.endsWith(sKey) || bKey.contains(sKey);
//            };
//            if (keys.stream().anyMatch(pk)) {
//                List<K> oKeys = keys.stream()
//                        .filter(pk)
//                        .collect(Collectors.toList());
//                oKeys.forEach(k -> {
//                    map.put(k, getVal(expiringMap, k));
//                    remove(expiringMap, k);
//                });
//            }
//        }
//        return map;
//    }
//
//    @Override
//    public V replace(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V newValue) {
//        V oldValue = expiringMap.replace(key, newValue);
//        ExpirePersistenceUtils.replacePersistence(key, oldValue, newValue);
//        return oldValue;
//    }
//
//    @Override
//    public Boolean putIfAbsent(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
//                               @NonNull String factoryBeanName) {
//        V v = expiringMap.putIfAbsent(key, value);
//        //null is put success but no null put failed
//        if (v == null) {
//            ExpirePersistenceUtils.putPersistence(key, value, null, null,
//                    factoryBeanName);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public Boolean putIfAbsentOfDuration(@NonNull ExpiringMap<K, V> expiringMap, @NonNull K key, @NonNull V value,
//                                         @NonNull Long duration, @NonNull TimeUnit timeUnit, @NonNull String factoryBeanName) {
//        synchronized (getLock()) {
//            if (hasKey(expiringMap, key)) {
//                return false;
//            } else {
//                putValOfDuration(expiringMap, key, value, duration, timeUnit, factoryBeanName);
//                return true;
//            }
//        }
//    }

}
