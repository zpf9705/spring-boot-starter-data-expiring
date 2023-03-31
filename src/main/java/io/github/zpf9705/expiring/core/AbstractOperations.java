package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.connection.ExpireConnection;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * This abstract class is mainly described {@link ExpireTemplate} Some behavioral methods
 *
 * @author zpf
 * @since 1.1.0
 **/
public abstract class AbstractOperations<K, V> {

    /*
     * @since 3.0.0
     */
    // utility methods for the template internal methods
    abstract class ValueDeserializingExpireCallback implements ExpireValueCallback<V> {

        private final K key;

        public ValueDeserializingExpireCallback(K key) {
            this.key = key;
        }

        @Override
        public V doInExpire(ExpireConnection connection) {
            /*
             * How to have a special transformation of key/value demand, can be operated in this department
             */
            byte[] bytes = inExpire(rawKey(this.key), connection);
            return deserializeValue(bytes);
        }

        @Nullable
        protected abstract byte[] inExpire(byte[] rawKey, ExpireConnection connection);
    }

    final ExpireTemplate<K, V> template;

    AbstractOperations(ExpireTemplate<K, V> expireTemplate) {
        this.template = expireTemplate;
    }

    @Nullable
    <T> T execute(ExpireValueCallback<T> callback, boolean composeException) {
        return template.execute(callback, composeException);
    }

    /**
     * To obtain the key serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<K> keySerializer() {
        return this.template.getKeySerializer();
    }

    /**
     * To obtain the value serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<V> valueSerializer() {
        return this.template.getValueSerializer();
    }

    /**
     * To get the operator
     *
     * @return {@link ExpireOperations}
     */
    public ExpireOperations<K, V> getOperations() {
        return this.template;
    }

    byte[] rawKey(K key) {
        Assert.notNull(key, "non null key required");
        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer().serialize(key);
    }

    byte[] rawValue(V value) {
        Assert.notNull(value, "non null key required");
        if (valueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }
        return valueSerializer().serialize(value);
    }

    V deserializeValue(byte[] value) {
        if (valueSerializer() == null) {
            return (V) value;
        }
        return valueSerializer().deserialize(value);
    }
}
