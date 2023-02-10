package io.github.zpf1997.expiring.core;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.reactivex.rxjava3.core.Single;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * <p>
 *  Template for ioc integration springboot of ExpiringMap
 * </p>
 * <p>
 * In order to better fit springboot {@link org.springframework.boot.autoconfigure.SpringBootApplication}
 * Here enclosed the template model about {@link AbstractExpireAccessor}
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
 *    .......
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
public class ExpireTemplate<K, V> extends AbstractExpireAccessor<K, V> implements ExpireOperations<K, V>, Clearable,
        Serializable {

    private static final long serialVersionUID = -8020854200126293536L;

    private ExpiringMap<K, V> expiringMap;

    private ExpiringSerializer<K> kExpiringSerializer;

    private ExpiringSerializer<V> vExpiringSerializer;

    private String factoryBeanName;

    private Integer maxSize;

    private Long defaultExpireTime;

    private TimeUnit defaultExpireTimeUnit;

    private ExpirationPolicy expirationPolicy;

    private static final Integer DEFAULT_MAX_SIZE = 20 * 50;

    private static final Long DEFAULT_EXPIRE_TIME = 30L;

    private static final TimeUnit DEFAULT_EXPIRE_TIME_UNIT = TimeUnit.SECONDS;

    private static final ExpirationPolicy DEFAULT_EXPIRATION_POLICY = ExpirationPolicy.ACCESSED;

    private final ValueOperations<K, V> valueOperations = new DefaultValueOperations<>(this);

    /**
     * NoArgsConstructor > postConstruct > afterPropertiesSet > @Bean
     */
    public ExpireTemplate() {}

    /**
     * @description: setting factory bean name
     * @param factoryBeanName
     */
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    /**
     * @param factoryBeanName
     * @description: In the name of the bean in the constructor
     */
    public ExpireTemplate(String factoryBeanName) {
        Assert.isTrue(StringUtils.isNotBlank(factoryBeanName), "factoryBeanName no be null");
        this.factoryBeanName = factoryBeanName;
    }

    /**
     * @param maxSize The maximum capacity
     * @return {@link ExpiringMap}
     * @description: Given the map one of the biggest capacity
     */
    public ExpireTemplate<K, V> acquireMaxSize(Integer maxSize) {
        Assert.isTrue(this.maxSize == null,
                "maxSize existing configuration values, please do not cover");
        this.maxSize = maxSize;
        return this;
    }

    /**
     * @param defaultExpireTime The default cache expiration time
     * @return {@link ExpiringMap}
     * @description: Given the map of a default cache expiration time
     */
    public ExpireTemplate<K, V> acquireDefaultExpireTime(Long defaultExpireTime) {
        Assert.isTrue(this.defaultExpireTime == null,
                "defaultExpireTime existing configuration values, please do not cover");
        this.defaultExpireTime = defaultExpireTime;
        return this;
    }

    /**
     * @param defaultExpireTimeUnit The default cache expiration time units
     * @return {@link ExpiringMap}
     * @description: Given the map of a default cache expiration time units
     */
    public ExpireTemplate<K, V> acquireDefaultExpireTimeTime(TimeUnit defaultExpireTimeUnit) {
        Assert.isTrue(this.defaultExpireTimeUnit == null,
                "defaultExpireTimeUnit existing configuration values, please do not cover");
        this.defaultExpireTimeUnit = defaultExpireTimeUnit;
        return this;
    }

    /**
     * @param expirationPolicy The default cache expiration expired strategy
     * @return {@link ExpiringMap}
     * @description: Given the map of a default cache expiration expired strategy
     */
    public ExpireTemplate<K, V> acquireDefaultExpirationPolicy(ExpirationPolicy expirationPolicy) {
        Assert.isTrue(this.expirationPolicy == null,
                "expirationPolicy existing configuration values, please do not cover");
        this.expirationPolicy = expirationPolicy;
        return this;
    }

    /**
     * @param templateKeyType
     * @description: Set the template key ExpiringSerializer
     */
    public void setKeySerializer(ExpiringSerializer<K> templateKeyType) {
        Assert.isTrue(this.kExpiringSerializer == null,
                "kExpiringSerializer existing configuration values, please do not cover");
        this.kExpiringSerializer = templateKeyType;
    }

    /**
     * @param templateValueType
     * @description: Set the template value ExpiringSerializer
     */
    public void setValueSerializer(ExpiringSerializer<V> templateValueType) {
        Assert.isTrue(this.vExpiringSerializer == null,
                "vExpiringSerializer existing configuration values, please do not cover");
        this.vExpiringSerializer = templateValueType;
    }

    /**
     * @description: Build a cache map
     */
    public void build() {
        Assert.isTrue(StringUtils.isNotBlank(factoryBeanName), "factoryBeanName no be null");
        Assert.notNull(kExpiringSerializer, "key Serializer no be null please setKeySerializer");
        Assert.notNull(vExpiringSerializer, "value Serializer no be null please setValueSerializer");
        if (this.maxSize == null || this.maxSize == 0) {
            this.maxSize = DEFAULT_MAX_SIZE;
        }
        if (this.defaultExpireTime == null || this.defaultExpireTime == 0L) {
            this.defaultExpireTime = DEFAULT_EXPIRE_TIME;
        }
        if (this.defaultExpireTimeUnit == null) {
            this.defaultExpireTimeUnit = DEFAULT_EXPIRE_TIME_UNIT;
        }
        if (this.expirationPolicy == null) {
            this.expirationPolicy = DEFAULT_EXPIRATION_POLICY;
        }
        this.expiringMap = ExpiringMap.builder()
                .maxSize(this.maxSize)
                .expiration(this.defaultExpireTime, this.defaultExpireTimeUnit)
                .expirationPolicy(this.expirationPolicy)
                .variableExpiration()
                .build();
    }

    /**
     * @description: Check whether the cache map initializes the initialization to avoid repetition
     */
    public void checkExpiringMapNoInit() {
        if (this.expiringMap != null) {
            throw new BeanInitializationException(
                    "If you initialize the expiring Map ahead of time ," +
                            "Please don't repeat use field assignment method ！"
            );
        }
    }

    /**
     * @param expirationListener {@link ExpirationListener}
     * @description: Increase the expired listeners
     * @see ExpirationListener
     */
    public void addExpiredListener(ExpirationListener<K, V> expirationListener) {
        if (this.expiringMap != null) {
            expiringMap.addExpirationListener(expirationListener);
        } else {
            throw new BeanInitializationException(
                    "expiringMap have no init"
            );
        }
    }

    /**
     * @return {@linkplain ExpiringMap}
     * @description: Access to the cache map
     */
    @Override
    public ExpiringMap<K, V> opsForExpire() {
        return expiringMap;
    }

    /**
     * @return {@linkplain ValueOperations}
     * @description: Access to the key value operator
     */
    @Override
    public ValueOperations<K, V> opsForValue() {
        return valueOperations;
    }

    @Override
    public Object execute(Callback<K, V> action) {
        return ExpireContextHolder.gain(() -> this.execute(action, true));
    }

    @Override
    public V putVal(K key, V value) {
        return ofType(() -> this.execute(e -> this.putVal(e, key, value, this.factoryBeanName)),
                vExpiringSerializer.serializerType(), true);
    }

    @Override
    public V putVal(K key, V value, Long duration, TimeUnit timeUnit) {
        return ofType(() -> this.execute(e -> this.putValOfDuration(e, key, value, duration, timeUnit
                        , this.factoryBeanName)),
                vExpiringSerializer.serializerType(), true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean putAll(Entry<K, V>... entries) {
        return ofType(() -> this.execute(e -> this.putAll(e, this.factoryBeanName, entries)), Boolean.class,
                true);
    }

    @Override
    public V getVal(K key) {
        return ofType(() -> this.execute(e -> this.getVal(e, key)),
                vExpiringSerializer.serializerType(), true);
    }

    @Override
    public Long getExpectedExpiration(K key) {
        return ofType(() -> this.execute(e -> this.getExpectedExpiration(e, key)),
                Long.class, true);
    }

    @Override
    public Long getExpiration(K key) {
        return ofType(() -> this.execute(e -> this.getExpiration(e, key)),
                Long.class, true);
    }

    @Override
    public K setExpiration(K key, Long duration, TimeUnit timeUnit) {
        return ofType(() -> this.execute(e -> this.setExpiration(e, key, duration, timeUnit)),
                kExpiringSerializer.serializerType(), true);
    }

    @Override
    public K resetExpiration(K key) {
        return ofType(() -> this.execute(e -> this.resetExpiration(e, key)),
                kExpiringSerializer.serializerType(), true);
    }

    @Override
    public Boolean hasKey(K key) {
        return ofType(() -> this.execute(e -> this.hasKey(e, key)),
                Boolean.class, true);
    }

    @Override
    public V remove(K key) {
        return ofType(() -> this.execute(e -> this.remove(e, key)),
                vExpiringSerializer.serializerType(), true);
    }

    @Override
    public Boolean removeAll() {
        this.clear();
        return true;
    }

    @Override
    public V replace(K key, V value) {
        return ofType(() -> this.execute(e -> this.replace(e, key, value)),
                vExpiringSerializer.serializerType(), true);
    }

    @Override
    public Boolean putIfAbsent(K key, V value) {
        return ofType(() -> this.execute(e -> this.putIfAbsent(e, key, value, this.factoryBeanName)),
                Boolean.class, true);
    }

    @Override
    public Boolean putIfAbsent(K key, V value, Long duration, TimeUnit timeUnit) {
        return ofType(() -> this.execute(e -> this.putIfAbsentOfDuration(e, key, value, duration, timeUnit
                        , this.factoryBeanName))
                , Boolean.class, true);
    }

    @Override
    public ExpiringSerializer<K> getKeySerializer() {
        return kExpiringSerializer;
    }

    @Override
    public ExpiringSerializer<V> getValueSerializer() {
        return vExpiringSerializer;
    }

    @Override
    public void clear() {
        if (this.expiringMap.isEmpty()) {
            return;
        }
        this.expiringMap.clear();
    }

    public void execute(Callback<K, V> action, boolean composeException) {
        Assert.notNull(action, "expireCallback can no be null !");
        Object o = null;
        try {
            o = action.doInExpire(this.expiringMap);
        } catch (Throwable e) {
            solverException(e, composeException);
        }
        ExpireContextHolder.temporary(o);
    }

    public <O> O ofType(Supplier<Object> o, Class<O> ofTypeClass, boolean composeSolveException) {
        Object source = o.get();
        //NOTE : source will be null but ofTypeClass is null return null
        if (source == null || ofTypeClass == null) {
            return null;
        }
        O target = null;
        try {
            target = Single.just(o.get())
                    .ofType(ofTypeClass)
                    .blockingGet();
        } catch (Throwable e) {
            solverException(e, composeSolveException);
        }
        return target;
    }

    public void solverException(Throwable e, boolean composeException) {
        Assert.notNull(e, "Throwable can no be null !");
        //CONSOLE EXPORT EXCEPTION
        Console.logger.info("expiring execute happened error {}", e.getMessage());
        if (composeException) {
            ExceptionUtil.wrapAndThrow(e);
        }
    }

    public StringExpiredTemplate toStringTemplate() {
        StringExpiredTemplate template = null;
        try {
            template = (StringExpiredTemplate) this;
        } catch (ClassCastException e) {
            //Strong turn abnormal <String,String> To do this conversion
            e.printStackTrace(System.err);
        }
        return template;
    }
}
