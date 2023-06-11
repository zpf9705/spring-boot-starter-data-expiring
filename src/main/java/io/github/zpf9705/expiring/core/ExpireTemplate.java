package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Helper class that simplifies Expiry data access code.
 * <p>
 * Performs automatic serialization/deserialization between the given objects
 * and the underlying binary data in the expiry store. By default, it uses
 * Generic String serialization for its objects(through {@link GenericStringExpiringSerializer}).
 * For String intensive operations consider the dedicated {@link StringExpireTemplate}.
 * <p>
 * The Expiry of the template model , imitate expireTemplate encapsulation mode
 * The cache operation way to connect assembly simulation for the connection,
 * and is equipped with a variety of connection factory
 * <p>
 * When the configuration is completed of this class are thread safe operation.
 * <p>
 * <b>his is the central class in Expiry support</b>
 *
 * @author zpf
 * @since 1.1.0
 **/
public class ExpireTemplate<K, V> extends ExpireAccessor implements ExpireOperations<K, V>, Serializable {

    private static final long serialVersionUID = -8020854200126293536L;
    @SuppressWarnings("rawtypes")
    private @CanNull ExpiringSerializer defaultSerializer;
    private boolean enableDefaultSerializer = true;
    private boolean initialized = false;

    private ExpiringSerializer<K> keySerialize;
    private ExpiringSerializer<V> valueSerialize;

    private final ValueOperations<K, V> valueOperations = new DefaultValueOperations<>(this);
    private final ExpirationOperations<K, V> expirationOperations = new DefaultExpirationOperations<>(this);

    /**
     * Constructs a new <code>ExpireTemplate</code> instance.
     */
    public ExpireTemplate() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        boolean defaultUsed = false;

        if (this.defaultSerializer == null) {
            this.defaultSerializer = new GenericStringExpiringSerializer();
        }

        if (this.enableDefaultSerializer) {

            if (this.keySerialize == null) {
                this.keySerialize = defaultSerializer;
                defaultUsed = true;
            }

            if (this.valueSerialize == null) {
                this.valueSerialize = defaultSerializer;
                defaultUsed = true;
            }
        }

        if (this.enableDefaultSerializer && defaultUsed) {
            AssertUtils.Operation.notNull(this.defaultSerializer, "defaultSerializer must initialized");
        }

        this.initialized = true;
    }

    @Override
    public <T> T execute(ExpireValueCallback<T> action, boolean composeException) {
        AssertUtils.Operation.isTrue(initialized, "Execute must before initialized");
        return execute(action, getHelperFactory(), composeException);
    }

    /**
     * Execute the plan had to adjust parameter calibration
     *
     * @param action           Expiry do action
     * @param factory          help factory
     * @param composeException Whether to merge exception
     * @param <T>              Return paradigm
     * @return return value be changed
     */
    public <T> T execute(ExpireValueCallback<T> action, ExpireHelperFactory factory, boolean composeException) {

        AssertUtils.Operation.notNull(factory, "ExpireConnectionFactory no be null");

        return execute(action, factory.getHelper(), composeException);
    }

    /**
     * Unified execute callback scheme, using try catch and handle exception to perform process monitoring,
     * once found to have abnormal situation a timely manner according to the number of times for a retry, if still
     * cannot successfully after retries,will prompt is given
     *
     * @param action           Expiry do action
     * @param helper           Expiry helper
     * @param composeException Whether to merge exception
     * @param <T>              return paradigm
     * @return return value be changed
     */
    public <T> T execute(ExpireValueCallback<T> action, ExpireHelper helper, boolean composeException) {
        /*
         * The callback encapsulation
         */
        Supplier<T> supplierRunner = () -> action.doInExpire(helper);
        //expiry apis runtime exception need throw
        T value;
        try {
            value = supplierRunner.get();
        } catch (Throwable e) {
            Console.error("Expiry abnormal operating the callback error [{}]", e.getMessage());
            if (composeException) {
                //Deviate from the custom exception exception thrown
                if (!(e instanceof ExpiringException)) {
                    throw new OperationsException(e);
                }
            }
            value = null;
        }
        return value;
    }

    /**
     * Whether the default serializer should be used. If not, any serializers not explicitly
     * set will remain null and values will not be serialized or deserialized.
     *
     * @param enableDefaultSerializer The above
     */
    public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
        this.enableDefaultSerializer = enableDefaultSerializer;
    }

    /**
     * Set the template key ExpiringSerializer
     *
     * @param keySerializer key Serializer
     */
    public void setKeySerializer(ExpiringSerializer<K> keySerializer) {
        AssertUtils.Operation.isTrue(this.keySerialize == null,
                "kExpiringSerializer existing configuration values, please do not cover");
        this.keySerialize = keySerializer;
    }

    /**
     * Set the template value ExpiringSerializer
     *
     * @param valueSerializer value Serializer
     */
    public void setValueSerializer(ExpiringSerializer<V> valueSerializer) {
        AssertUtils.Operation.isTrue(this.valueSerialize == null,
                "vExpiringSerializer existing configuration values, please do not cover");
        this.valueSerialize = valueSerializer;
    }

    @Override
    public ExpiringSerializer<K> getKeySerializer() {
        return this.keySerialize;
    }

    @Override
    public ExpiringSerializer<V> getValueSerializer() {
        return this.valueSerialize;
    }

    /**
     * Access to the key value operator
     *
     * @return {@linkplain ValueOperations}
     */
    @Override
    public ValueOperations<K, V> opsForValue() {
        return this.valueOperations;
    }

    /**
     * Access to the key value operator
     *
     * @return {@link  ExpirationOperations}
     */
    @Override
    public ExpirationOperations<K, V> opsExpirationOperations() {
        return this.expirationOperations;
    }

    @CanNull
    @Override
    public Boolean delete(K key) {
        Long result = execute((connection) -> connection.delete(
                this.rawKey(key)
        ), true);
        return result != null && result.intValue() == 1;
    }

    @CanNull
    @Override
    public Long delete(Collection<K> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        return this.execute((connection) -> connection.delete(
                this.rawKeys(keys)
        ), true);
    }

    @Override
    public Map<K, V> deleteType(K key) {

        Map<byte[], byte[]> map = this.execute((connection) -> connection.deleteType(
                this.rawKey(key)
        ), true);

        if (map == null || map.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K, V> ma = new HashMap<>();
        for (byte[] keyBytes : map.keySet()) {
            byte[] valueBytes = map.get(keyBytes);
            ma.put(this.keySerialize.deserialize(keyBytes), this.valueSerialize.deserialize(valueBytes));
        }
        return ma;
    }

    @Override
    public Boolean deleteAll() {
        return this.execute(ExpireKeyCommands::deleteAll, true);
    }

    @Override
    public Boolean exist(K key) {
        return this.execute((connection) -> connection.hasKey(
                this.rawKey(key)
        ), true);
    }

    private byte[] rawKey(K key) {
        AssertUtils.Operation.notNull(key, "Non null key required");
        byte[] v;
        if (this.keySerialize != null) {
            v = this.keySerialize.serialize(key);
        } else {
            if (key instanceof byte[]) {
                return (byte[]) key;
            } else {
                v = null;
            }
        }
        return v;
    }

    private byte[][] rawKeys(Collection<K> keys) {
        final byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;
        for (K key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }

    private byte[] rawValue(V value) {
        AssertUtils.Operation.notNull(value, "Non null value required");
        byte[] v;
        if (this.valueSerialize != null) {
            v = this.valueSerialize.serialize(value);
        } else {
            if (value instanceof byte[]) {
                v = (byte[]) value;
            } else {
                v = null;
            }
        }
        return v;
    }
}
