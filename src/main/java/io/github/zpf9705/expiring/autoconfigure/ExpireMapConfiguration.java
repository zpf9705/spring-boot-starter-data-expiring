package io.github.zpf9705.expiring.autoconfigure;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.zpf9705.expiring.banner.ExpireMapBanner;
import io.github.zpf9705.expiring.banner.ExpireStartUpBannerExecutor;
import io.github.zpf9705.expiring.banner.StartUpBanner;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfigurationCustomizer;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionFactory;
import io.github.zpf9705.expiring.core.ExpireProperties;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.listener.ExpiringAsyncListener;
import io.github.zpf9705.expiring.listener.ExpiringSyncListener;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Expire connection configuration using expireMap {@link net.jodah.expiringmap.ExpiringMap}
 *
 * @author zpf
 * @since 3.0.0
 */
@Configuration(
        proxyBeanMethods = false
)
@EnableConfigurationProperties(ExpireProperties.class)
public class ExpireMapConfiguration extends ExpireConnectionConfiguration implements ExpireBannerDisplayDevice,
        EnvironmentAware {

    private Environment environment;

    static final String SYNC_SIGN = "SYNC";

    static final String ASYNC_SIGN = "ASYNC";

    static String EXPIRED_METHOD_NAME;

    static final Predicate<Method> METHOD_PREDICATE = (s) -> EXPIRED_METHOD_NAME.equals(s.getName());

    static {
        /*
         * Take the default Expiration Listener the class name of the first method
         */
        Method[] methods = ExpirationListener.class.getMethods();
        if (ArrayUtil.isNotEmpty(methods)) {
            EXPIRED_METHOD_NAME = methods[0].getName();
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
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NonNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    @NonNull
    public Class<?> getSourceClass() {
        return ExpiringMap.class;
    }

    @Override
    @NonNull
    public StartUpBanner getStartUpBanner() {
        return new ExpireMapBanner();
    }

    @Bean
    @ConditionalOnMissingBean({ExpireConnectionFactory.class})
    public ExpireMapConnectionFactory expireMapConnectionFactory(
            ObjectProvider<ExpireMapClientConfigurationCustomizer> buildCustomizer) {
        ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder = ExpireMapClientConfiguration.builder();
        buildCustomizer.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return new ExpireMapConnectionFactory(builder.build());
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
            if (!CollectionUtils.isEmpty(listenerMap)) {
                List<ExpirationListener> sync = listenerMap.get(SYNC_SIGN);
                if (!CollectionUtils.isEmpty(sync)) {
                    sync.forEach(builder::addSyncExpiredListener);
                }
                List<ExpirationListener> async = listenerMap.get(ASYNC_SIGN);
                if (!CollectionUtils.isEmpty(async)) {
                    async.forEach(builder::addASyncExpiredListener);
                }
            }
        };
    }

    @SuppressWarnings({"rawtypes"})
    public Map<String, List<ExpirationListener>> findExpirationListener() {
        //obtain listing packages path
        String listeningPackages = getProperties().getExpiringMap().getListeningPackages();
        if (StringUtils.isBlank(listeningPackages)) {
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
        if (CollectionUtils.isEmpty(subTypesOf)) {
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
            ExpirationListener listener = doCreateExpirationListener(listenerClass);
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

    @SuppressWarnings({"rawtypes"})
    private ExpirationListener doCreateExpirationListener(Class<? extends ExpirationListener> listenerClass) {
        if (listenerClass == null) return null;
        ExpirationListener listener;
        try {
            listener = ReflectUtil.newInstance(listenerClass);
        } catch (Throwable e) {
            listener = null;
        }
        return listener;
    }
}
