package io.github.zpf9705.expiring.listener;

import net.jodah.expiringmap.ExpirationListener;

/**
 * default Expiring Load Listener of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 2.0.1
 */
public abstract class MessageExpiringListener implements ExpirationListener<Object, Object> {

    @Override
    public void expired(Object key, Object value) {
        onMessage(new Message(key, value));
    }

    public abstract void onMessage(Message message);
}
