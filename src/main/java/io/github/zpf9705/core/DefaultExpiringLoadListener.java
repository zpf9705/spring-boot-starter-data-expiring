package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpirationListener;

/**
 * <p>
 *    default Expiring Load Listener of key{@link String} and value {@link String}
 * <p>
 *
 * @author zpf
 * @since 2.0.1
 */
public abstract class DefaultExpiringLoadListener implements ExpirationListener<String, String> {

    @Override
    public abstract void expired(String key, String value);
}
