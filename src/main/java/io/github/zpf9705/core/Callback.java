package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpiringMap;


/**
 * <p>
 *   interface for call back
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Callback<K, V> {

    Object doInExpire(ExpiringMap<K, V> expiringMap);
}
