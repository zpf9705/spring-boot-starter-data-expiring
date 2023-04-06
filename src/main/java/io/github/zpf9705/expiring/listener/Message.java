package io.github.zpf9705.expiring.listener;

import org.springframework.lang.NonNull;
import org.springframework.util.SerializationUtils;

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
        this.key = SerializationUtils.deserialize(key);
        this.value = SerializationUtils.deserialize(value);
    }

    @NonNull
    public Object getKey() {
        return key;
    }

    @NonNull
    public Object getValue() {
        return value;
    }

    public static Message serial(@NonNull byte[] key, @NonNull byte[] value) {
        return new Message(key, value);
    }
}
