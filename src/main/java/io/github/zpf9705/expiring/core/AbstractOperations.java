package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.util.AssertUtils;

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
        public V doInExpire(ExpireHelper helper) {
            /*
             * How to have a special transformation of key/value demand, can be operated in this department
             */
            byte[] bytes = inExpire(rawKey(this.key), helper);
            return deserializeValue(bytes);
        }

        @CanNull
        protected abstract byte[] inExpire(byte[] rawKey, ExpireHelper helper);
    }

    final ExpireTemplate<K, V> template;

    AbstractOperations(ExpireTemplate<K, V> expireTemplate) {
        this.template = expireTemplate;
    }

    @CanNull
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
        AssertUtils.Operation.notNull(key, "Non null key required");
        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer().serialize(key);
    }

    byte[] rawValue(V value) {
        AssertUtils.Operation.notNull(value, "Non null key required");
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
