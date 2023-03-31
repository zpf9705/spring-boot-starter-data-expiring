package io.github.zpf9705.expiring.autoconfigure;

import cn.hutool.aop.proxy.SpringCglibProxyFactory;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import io.github.zpf9705.expiring.banner.ExpireStartUpBanner;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfigurationCustomizer;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionFactory;
import io.github.zpf9705.expiring.core.ExpireTemplate;
import io.github.zpf9705.expiring.listener.ExpiringListener;
import io.github.zpf9705.expiring.listener.ExpiringListeners;
import io.github.zpf9705.expiring.listener.PersistenceExpiringCallback;
import io.github.zpf9705.expiring.core.logger.Console;
import net.jodah.expiringmap.ExpirationListener;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Expire connection configuration using ExpireMap.
 *
 * @author zpf
 * @since jdk.spi.version-2022.11.18 - [2023-03-31 13:06]
 */
@Configuration(
        proxyBeanMethods = false
)
@EnableConfigurationProperties(ExpireMapCacheProperties.class)
public class ExpireMapConfiguration implements InitializingBean, EnvironmentAware {

    private final ExpireMapCacheProperties expireMapCacheProperties;

    private Environment environment;

    public ExpireMapConfiguration(ExpireMapCacheProperties expireMapCacheProperties) {
        this.expireMapCacheProperties = expireMapCacheProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBanner.bannerPrinter(environment, ExpireAutoConfiguration.class);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnMissingBean({ExpireConnectionFactory.class})
    public ExpireMapConnectionFactory expireMapConnectionFactory(
            ObjectProvider<ExpireMapClientConfigurationCustomizer> buildCustomizer) {
        ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder
                = ExpireMapClientConfiguration.builder();
        buildCustomizer.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return new ExpireMapConnectionFactory(builder.build());
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public ExpireMapClientConfigurationCustomizer customizer() {
        return c -> {
            ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder =
                    c.acquireMaxSize(expireMapCacheProperties.getMaxSize())
                            .acquireDefaultExpireTime(expireMapCacheProperties.getDefaultExpireTime())
                            .acquireDefaultExpireTimeUnit(expireMapCacheProperties.getDefaultExpireTimeUnit())
                            .acquireDefaultExpirationPolicy(expireMapCacheProperties.getExpirationPolicy());
            List<ExpirationListener> expirationListener = findExpirationListener();
            if (!CollectionUtils.isEmpty(expirationListener)) {
                expirationListener.forEach(builder::addExpiredListener);
            }
        };
    }

    @SuppressWarnings({"rawtypes"})
    public List<ExpirationListener> findExpirationListener() {
        //obtain listing packages path
        String listeningPackages = expireMapCacheProperties.getListeningPackages();
        if (StringUtils.isBlank(listeningPackages)) {
            Console.info(
                    "no provider listening scan path ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return Collections.emptyList();
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
                    "no provider implementation ExpiringLoadListener class ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return Collections.emptyList();
        }
        List<ExpirationListener> expirationListeners = new ArrayList<>();
        final Predicate<Method> filter = (s) -> "expired".equals(s.getName());
        for (Class<? extends ExpirationListener> aClass : subTypesOf) {
            if (Modifier.isAbstract(aClass.getModifiers())) {
                continue;
            }
            Method target;
            if (Arrays.stream(aClass.getMethods()).noneMatch(filter)) {
                continue;
            } else {
                target = Arrays.stream(aClass.getMethods()).filter(filter)
                        .findFirst()
                        .orElse(null);
            }
            if (target == null) {
                continue;
            }
            ExpirationListener listener =
                    new SpringCglibProxyFactory().proxy(ReflectUtil.newInstance(aClass),
                            PersistenceExpiringCallback.class);
            expirationListeners.add(listener);
        }
        return expirationListeners;
    }
}
