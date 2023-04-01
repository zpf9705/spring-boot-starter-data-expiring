package io.github.zpf9705.expiring.listener;

import net.jodah.expiringmap.ExpirationListener;

/**
 * default Expiring Load Listener of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 2.0.1
 */
public abstract class MessageExpiringListener implements ExpirationListener<byte[], byte[]> {

    @Override
    public void expired(byte[] key, byte[] value) {
        onMessage(new Message(key, value));
    }

    /**
     * Will into a byte array to {@link Message}
     *
     * @param message be a {@code message}
     */
    public abstract void onMessage(Message message);
}
