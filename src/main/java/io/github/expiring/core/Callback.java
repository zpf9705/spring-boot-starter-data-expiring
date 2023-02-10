package io.github.expiring.core;

import net.jodah.expiringmap.ExpiringMap;

/**
 *
 *  interface for call back
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Callback<K, V> {

    Object doInExpire(ExpiringMap<K, V> expiringMap);
}
