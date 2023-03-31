package io.github.zpf9705.expiring.listener;

/**
 *
 * @author zpf
 * @since 3.0.0
 */
public class Message {

    private final Object key;
    private final Object value;

    public Message(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
