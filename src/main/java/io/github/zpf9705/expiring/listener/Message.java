package io.github.zpf9705.expiring.listener;

import org.springframework.util.SerializationUtils;

import java.io.Serializable;

/**
 * Expiration entry for it model
 *
 * @author zpf
 * @since 3.0.0
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -8830456426162230361L;

    private final Object key;

    private final Object value;

    public Message(byte[] key, byte[] value) {
        this.key = SerializationUtils.deserialize(key);
        this.value = SerializationUtils.deserialize(value);
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
