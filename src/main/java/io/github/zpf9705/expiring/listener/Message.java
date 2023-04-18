package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.SerialUtils;
import java.io.Serializable;

/**
 * Key/value pair deserialized object
 *
 * @author zpf
 * @since 3.0.0
 */
public final class Message implements Serializable {

    private static final long serialVersionUID = -8830456426162230361L;

    private final Object key;

    private final Object value;

    private Message(byte[] key, byte[] value) {
        this.key = SerialUtils.deserialize(key);
        this.value = SerialUtils.deserialize(value);
    }

    @NotNull
    public Object getKey() {
        return key;
    }

    @NotNull
    public Object getValue() {
        return value;
    }

    public static Message serial(@NotNull byte[] key, @NotNull byte[] value) {
        return new Message(key, value);
    }
}
