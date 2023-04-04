package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.core.error.OperationsException;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.reactivex.rxjava3.core.Single;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>
 * Template for ioc integration springboot of ExpiringMap
 * </p>
 * <p>
 * In order to better fit springboot {@link org.springframework.boot.autoconfigure.SpringBootApplication}
 * Here enclosed the template model about {@link ExpireAccessor}
 * ----------------------
 * {@link ExpiringMap}
 * ------ english introduce -------
 * Max Size: the maximum length of the map, add the 1001th entry, can lead to the first expired immediately (even if not to expiration time).
 * Expiration: expiration time and expired unit, set the expiration time, is permanent.
 * The use of expiration Policy: expiration policies.
 * CREATED: when each update element, the countdown reset date.
 * ACCESSED: in every visit elements, the countdown reset date.
 * Variable Expiration: allows you to update the Expiration time value, if not set variable Expiration.
 * Are not allowed to change the expiration time, once the executive change the expiration time will throw an Unsupported Operation Exception.
 * Expiration Listener: synchronous expired reminders
 * Async Expiration Listener: asynchronous expired reminders
 * Entry Loader: lazy loading, if the key does not exist when calling the get method to create the default value
 * ----------------------
 * ------ 中文 解析 -------
 * maxSize:map的最大长度,添加第1001个entry时,会导致第1个马上过期(即使没到过期时间)
 * expiration:过期时间和过期单位,设置过期时间,则永久有效.
 * expirationPolicy:过期策略的使用
 * CREATED：  在每次更新元素时，过期倒计时清零
 * ACCESSED： 在每次访问元素时，过期倒计时清零
 * variableExpiration:允许更新过期时间值,如果不设置variableExpiration
 * 不允许更改过期时间,一旦执行更改过期时间的操作则会抛出UnsupportedOperationException异常
 * expirationListener:同步过期提醒
 * asyncExpirationListener:异步过期提醒
 * entryLoader:懒加载,调用get方法时若key不存在创建默认value
 * .......
 * </p>
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
    public <T> T execute(ExpireValueCallback<T> action, boolean composeException) {

        AssertUtils.Operation.isTrue(initialized, "Execute must before initialized");

        ExpireConnectionFactory connectionFactory = getConnectionFactory();

        AssertUtils.Operation.notNull(connectionFactory, "ConnectionFactory no be null");

        AssertUtils.Operation.hasText(this.factoryBeanName, "FactoryBeanName no be null");

        T result;
        try {
            result = action.doInExpire(connectionFactory.getConnection(), this.factoryBeanName);

        } catch (Throwable e) {
            solverException(e, composeException);
            result = null;
        }

        return result;
    }

    /**
     * Whether or not the default serializer should be used. If not, any serializers not explicitly
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
        Long result = execute((connection, f) -> connection.delete(
                this.rawKey(key)
        ), true);
        return result != null && result.intValue() == 1;
    }

    @Nullable
    @Override
    public Long delete(Collection<K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return this.execute((connection, f) -> connection.delete(
                this.rawKeys(keys)
        ), true);
    }

    @Override
    public Map<K, V> deleteType(K key) {

        Map<byte[], byte[]> map = this.execute((connection, f) -> connection.deleteType(
                this.rawKey(key)
        ), true);

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
        return this.execute((connection, f) -> connection.deleteAll(), true);
    }

    @Override
    public Boolean exist(K key) {
        return this.execute((connection, f) -> connection.hasKey(
                this.rawKey(key)
        ), true);
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

    /**
     * exchange special class type
     *
     * @param o                     supplier
     * @param ofTypeClass           type class
     * @param composeSolveException Whether solve exception
     * @param <O>                   You can specify the paradigm
     * @return exchange obj
     */
    public <O> O ofType(Supplier<Object> o, Class<O> ofTypeClass, boolean composeSolveException) {
        Object source = o.get();
        //NOTE : source will be null but ofTypeClass is null return null
        if (source == null || ofTypeClass == null) {
            return null;
        }
        O target = null;
        try {
            /*
             * update repeat call value to threadLocal
             * @since 2.1.1-complete
             */
            target = Single.just(source)
                    .ofType(ofTypeClass)
                    .blockingGet();
        } catch (Throwable e) {
            solverException(e, composeSolveException);
        }
        return target;
    }

    /**
     * solver exception way
     *
     * @param e                exception
     * @param composeException whether solve exception
     */
    public void solverException(Throwable e, boolean composeException) throws OperationsException {
        AssertUtils.Operation.notNull(e, "Exception handling object information can't be null");
        Console.info("The cache implementation errors [{}]", e.getMessage());
        if (composeException) {
            throw new OperationsException(e);
        }
    }
}
