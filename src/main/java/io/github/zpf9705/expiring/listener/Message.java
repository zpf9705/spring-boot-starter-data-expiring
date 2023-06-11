package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.SerialUtils;

import java.io.Serializable;

/**
 * Deserialize objects with pair
 *
 * @author zpf
 * @since 3.0.0
 */
public final class Message implements Serializable, MessageExpiryCapable {

    private static final long serialVersionUID = -8830456426162230361L;

    private final byte[] key;

    private final byte[] value;

    private final Object keySerialize;

    private final Object valueSerialize;

    private Message(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.keySerialize = SerialUtils.deserialize(key);
        this.valueSerialize = SerialUtils.deserialize(value);
    }

    @Override
    @NotNull
    public byte[] getByteKey() {
        return key;
    }

    @Override
    @NotNull
    public byte[] getByteValue() {
        return value;
    }

    @Override
    @NotNull
    public Object getKey() {
        return keySerialize;
    }

    @Override
    @NotNull
    public Object getValue() {
        return valueSerialize;
    }

    /**
     * The key value deserialization
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     * @return {@link Message}
     */
    public static Message serial(@NotNull byte[] key, @NotNull byte[] value) {
        Message message;
        try {
            message = new Message(key, value);
        } catch (Throwable e) {
            throw new OperationsException("Message serial error with msg :[ " + e.getMessage() + " ]");
        }
        return message;
    }
}
