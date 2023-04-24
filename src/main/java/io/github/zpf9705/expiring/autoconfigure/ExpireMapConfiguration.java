package io.github.zpf9705.expiring.autoconfigure;

import cn.hutool.core.util.ReflectUtil;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapClientConfiguration;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapClientConfigurationCustomizer;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapHelperFactory;
import io.github.zpf9705.expiring.listener.ExpiringAsyncListener;
import io.github.zpf9705.expiring.listener.ExpiringSyncListener;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.CollectionUtils;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * Expire connection configuration using expireMap
 * {@link net.jodah.expiringmap.ExpiringMap}
 * ------ english introduce -------
 * Max Size: the maximum length of the map, add the 1001 th entry, can lead to the first expired
 * immediately (even if not to expiration time).
 * Expiration: expiration time and expired unit, set the expiration time, is permanent.
 * The use of expiration Policy: expiration policies.
 * CREATED: when each update element, the countdown reset date.
 * ACCESSED: in every visit elements, the countdown reset date.
 * Variable Expiration: allows you to update the Expiration time value, if not set variable Expiration.
 * Are not allowed to change the expiration time, once the executive change the expiration time
 * will throw an Unsupported Operation Exception.
 * Expiration Listener: synchronous expired reminders
 * Async Expiration Listener: asynchronous expired reminders
 * Entry Loader: lazy loading, if the key does not exist when calling the get method to create
 * the default value
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
 *
 * @author zpf
 * @since 3.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ExpiringMap.class})
@ConditionalOnProperty(
        name = "spring.data.expiry.operation-type",
        havingValue = "expire_map",
        matchIfMissing = true

)
public class ExpireMapConfiguration extends ExpireHelperConfiguration implements ExpireBannerDisplayDevice,
        EnvironmentAware {

    private Environment environment;

    static final String SYNC_SIGN = "SYNC";

    static final String ASYNC_SIGN = "ASYNC";

    static String EXPIRED_METHOD_NAME;

    static Predicate<Method> METHOD_PREDICATE;

    static {
        /*
         * Take the default Expiration Listener the class name of the first method
         */
        Method[] methods = ExpirationListener.class.getMethods();
        if (ArrayUtils.simpleNotEmpty(methods)) {
            EXPIRED_METHOD_NAME = methods[0].getName();
            //Matching assertion method static load
            METHOD_PREDICATE = (s) -> EXPIRED_METHOD_NAME.equals(s.getName());
        }
    }

    public ExpireMapConfiguration(ExpireProperties properties) {
        super(properties);
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBannerExecutor.printBanner(environment, getStartUpBanner(), sourceClass, out);
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    @NotNull
    public Class<?> getSourceClass() {
        return ExpiringMap.class;
    }

    @Override
    @NotNull
    public StartUpBanner getStartUpBanner() {
        return new ExpireMapBanner();
    }

    @Bean
    @ConditionalOnMissingBean({ExpireHelperFactory.class})
    public ExpireHelperFactory expireMapConnectionFactory(
            ObjectProvider<ExpireMapClientConfigurationCustomizer> buildCustomizer) {
        ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder = ExpireMapClientConfiguration.builder();
        buildCustomizer.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return new ExpireMapHelperFactory(builder.build());
    }

    @Bean("expireMap::expireMapClientCustomizer")
    @SuppressWarnings("rawtypes")
    public ExpireMapClientConfigurationCustomizer expireMapClientCustomizer() {
        ExpireProperties expireProperties = getProperties();
        return c -> {
            ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder =
                    c.acquireMaxSize(expireProperties.getExpiringMap().getMaxSize())
                            .acquireDefaultExpireTime(expireProperties.getDefaultExpireTime())
                            .acquireDefaultExpireTimeUnit(expireProperties.getDefaultExpireTimeUnit())
                            .acquireDefaultExpirationPolicy(expireProperties.getExpiringMap().getExpirationPolicy());
            Map<String, List<ExpirationListener>> listenerMap = findExpirationListener();
            if (CollectionUtils.simpleNotEmpty(listenerMap)) {
                List<ExpirationListener> sync = listenerMap.get(SYNC_SIGN);
                if (CollectionUtils.simpleNotEmpty(sync)) {
                    sync.forEach(builder::addSyncExpiredListener);
                }
                List<ExpirationListener> async = listenerMap.get(ASYNC_SIGN);
                if (CollectionUtils.simpleNotEmpty(async)) {
                    async.forEach(builder::addASyncExpiredListener);
                }
            }
        };
    }

    @SuppressWarnings({"rawtypes"})
    public Map<String, List<ExpirationListener>> findExpirationListener() {
        //obtain listing packages path
        String[] listeningPackages = getProperties().getExpiringMap().getListeningPackages();
        if (ArrayUtils.simpleIsEmpty(listeningPackages)) {
            Console.info(
                    "no provider listening scan path ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return Collections.emptyMap();
        }
        //reflection find packages
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(listeningPackages)
        );
        //reflection find ExpiringLoadListener impl
        Set<Class<? extends ExpirationListener>> subTypesOf =
                reflections.getSubTypesOf(ExpirationListener.class);
        if (CollectionUtils.simpleIsEmpty(subTypesOf)) {
            Console.info(
                    "No provider implementation ExpiringLoadListener class ," +
                            "so ec no can provider binding Expiration Listener"
            );
            return Collections.emptyMap();
        }
        List<ExpirationListener> sync = new ArrayList<>();
        List<ExpirationListener> async = new ArrayList<>();
        Map<String, List<ExpirationListener>> listenerMap = new HashMap<>();
        for (Class<? extends ExpirationListener> listenerClass : subTypesOf) {
            if (Modifier.isAbstract(listenerClass.getModifiers())) {
                continue;
            }
            Method target;
            if (Arrays.stream(listenerClass.getMethods()).noneMatch(METHOD_PREDICATE)) {
                continue;
            } else {
                target = Arrays.stream(listenerClass.getMethods()).filter(METHOD_PREDICATE)
                        .findFirst()
                        .orElse(null);
            }
            if (target == null) {
                continue;
            }
            ExpirationListener listener;
            try {
                listener = ReflectUtil.newInstance(listenerClass);
            } catch (Throwable e) {
                Console.warn("[" + listenerClass.getName() + "] newInstanceForNoArgs failed : [" + e.getMessage() + "]");
                continue;
            }
            //Synchronous monitoring is preferred
            ExpiringSyncListener syncListener = listenerClass.getAnnotation(ExpiringSyncListener.class);
            if (syncListener == null) {
                ExpiringAsyncListener asyncListener = listenerClass.getAnnotation(ExpiringAsyncListener.class);
                if (asyncListener != null) {
                    async.add(listener);
                }
            } else {
                sync.add(listener);
            }
        }
        listenerMap.put(SYNC_SIGN, sync);
        listenerMap.put(ASYNC_SIGN, async);
        return listenerMap;
    }
}
