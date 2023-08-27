package io.github.zpf9705.expiring.autoconfigure;

import cn.hutool.core.util.ReflectUtil;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapClientConfiguration;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapClientConfigurationCustomizer;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapHelperFactory;
import io.github.zpf9705.expiring.listener.ExpiringAsyncListener;
import io.github.zpf9705.expiring.listener.ExpiringSyncListener;
import io.github.zpf9705.expiring.logger.Console;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.ScanUtils;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
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
 * One of the optional caches for this component {@link net.jodah.expiringmap.ExpiringMap}
 * <p>
 * The following is an explanation of important parameters :
 * {@link ExpireProperties#getExpiringMap()}
 * <p>
 * {@code Max Size } : the maximum length of the map, add the 1001 th entry, can lead to the first expired
 * immediately (even if not to expiration time).
 * <p>
 * {@code Expiration }: expiration time and expired unit, set the expiration time, is permanent.
 * The use of expiration Policy: expiration policies.
 * <p>
 * {@code CREATED}: when each update element, the countdown reset date.
 * <p>
 * {@code ACCESSED }: in every visit elements, the countdown reset date.
 * <p>
 * {@code Variable Expiration }: allows you to update the Expiration time value, if not set variable Expiration.
 * Are not allowed to change the expiration time, once the executive change the expiration time
 * will throw an Unsupported Operation Exception.
 * <p>
 * {@code Expiration Listener }: Synchronous listener , Need to implement {@link ExpirationListener}
 * and annotate {@link ExpiringSyncListener} to achieve synchronous expiration notification
 * <p>
 * {@code Async Expiration Listener } : Asynchronous listener , Need to implement {@link ExpirationListener}
 * and annotate {@link ExpiringAsyncListener} to achieve synchronous expiration notification
 * <p>
 * {@code Entry Loader } : lazy loading, if the key does not exist when calling the get method to create
 * the default value
 * <p>
 * Provide a configuration for you to load relevant build configurations related to {@link ExpiringMap},
 * and select this cache configuration to load and use in the main assembly class by default
 *
 * @author zpf
 * @since 3.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ExpiringMap.class})
@ConditionalOnProperty(
        name = "spring.data.expiry.client",
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
        Set<Class<ExpirationListener>> subTypesOf =
                ScanUtils.getSubTypesOf(ExpirationListener.class, listeningPackages);
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
