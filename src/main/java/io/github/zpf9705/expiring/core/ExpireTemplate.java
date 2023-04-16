package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.core.error.ExpiringException;
import io.github.zpf9705.expiring.core.error.OperationsException;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
    private @Nullable ExpiringSerializer defaultSerializer;
    private boolean enableDefaultSerializer = true;
    private boolean initialized = false;

    private ExpiringSerializer<K> keySerialize;
    private ExpiringSerializer<V> valueSerialize;
    private String factoryBeanName;

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
            Assert.notNull(this.defaultSerializer, "defaultSerializer must initialized");
        }

        this.initialized = true;
    }

    @Override
    public <T> T execute(ExpireValueCallback<T> action, boolean holdFactoryName, boolean composeException) {
        AssertUtils.Operation.isTrue(initialized, "Execute must before initialized");
        return execute(action, holdFactoryName, composeException, getFactoryBeanName());
    }

    /**
     * Execute the plan had to adjust parameter calibration
     *
     * @param action           Expiry do action
     * @param holdFactoryName  Set template ioc name in hold
     * @param composeException Whether to merge exception
     * @param factoryBeanName  Template factory bean name
     * @param <T>              Return paradigm
     * @return return value be changed
     */
    public <T> T execute(ExpireValueCallback<T> action, boolean holdFactoryName, boolean composeException,
                         String factoryBeanName) {

        AssertUtils.Operation.hasText(factoryBeanName, "FactoryBeanName no be null");

        /*
         * Because may be executed asynchronously, clean up where you need to perform
         */
        if (holdFactoryName) ExpireFactoryNameHolder.setFactoryName(factoryBeanName);

        ExpireHelperFactory factory = getHelperFactory();

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
        } finally {
            //Release the template name of factory
            ExpireFactoryNameHolder.restFactoryNamedContent();
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
     * setting factory bean name
     *
     * @param factoryBeanName ioc bean name
     */
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    /**
     * get factory bean name
     *
     * @return ioc bean name
     */
    public String getFactoryBeanName() {
        return this.factoryBeanName;
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

    @Nullable
    @Override
    public Boolean delete(K key) {
        Long result = execute((connection) -> connection.delete(
                this.rawKey(key)
        ), false, true);
        return result != null && result.intValue() == 1;
    }

    @Nullable
    @Override
    public Long delete(Collection<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return this.execute((connection) -> connection.delete(
                this.rawKeys(keys)
        ), false, true);
    }

    @Override
    public Map<K, V> deleteType(K key) {

        Map<byte[], byte[]> map = this.execute((connection) -> connection.deleteType(
                this.rawKey(key)
        ), false, true);

        if (CollectionUtils.isEmpty(map)) {
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
        return this.execute(ExpireKeyCommands::deleteAll, false, true);
    }

    @Override
    public Boolean exist(K key) {
        return this.execute((connection) -> connection.hasKey(
                this.rawKey(key)
        ), false, true);
    }

    private byte[] rawKey(K key) {
        Assert.notNull(key, "non null key required");
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
        Assert.notNull(value, "non null value required");
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
