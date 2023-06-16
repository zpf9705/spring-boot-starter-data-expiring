package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.help.RecordActivationCenter;
import io.github.zpf9705.expiring.util.AbleUtils;

/**
 * Default abstract Expiring Load Listener container of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class MessageExpiringContainer implements ExpirationBytesBlocker {

    private static final long serialVersionUID = -3836350084112628115L;

    private MessageExpiryCapable capable;

    @Override
    public void expired(byte[] key, byte[] value) {
        this.capable = Message.serial(key, value);
        //notify
        onMessage(this.capable);
        //clean Persistence with key and value
        AbleUtils.close(this);
    }

    @Override
    public void close() {
        RecordActivationCenter.getSingletonCenter().cleanSupportingElements(capable);
    }

    /**
     * Will into a byte array to {@link Message}
     *
     * @param capable be a {@code message}
     */
    public abstract void onMessage(MessageExpiryCapable capable);
}
