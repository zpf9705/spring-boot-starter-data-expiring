package io.github.zpf9705.expiring.listener;

import net.jodah.expiringmap.ExpirationListener;

/**
 * default Expiring Load Listener container of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class MessageExpiringContainer implements ExpirationListener<byte[], byte[]> {

    @Override
    public void expired(byte[] key, byte[] value) {
        onMessage(Message.serial(key,value));
    }

    /**
     * Will into a byte array to {@link Message}
     *
     * @param message be a {@code message}
     */
    public abstract void onMessage(Message message);
}
