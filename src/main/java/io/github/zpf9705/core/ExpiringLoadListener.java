package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpirationListener;

/**
 * <p>
 * Overdue loading notice of {@link ExpirationListener}
 * <p>
 *
 * @author zpf
 * @since 2.0.1
 */
public interface ExpiringLoadListener<K, V> extends ExpirationListener<K, V> {

    /**
     * default expired way
     *
     * @param key   Expired key
     * @param value Expired value
     */
    @Override
    default void expired(K key, V value) {
        load(key, value);
    }

    /**
     * load expired key value
     *
     * @param key   Expired key
     * @param value Expired value
     */
    void load(K key, V value);
}
