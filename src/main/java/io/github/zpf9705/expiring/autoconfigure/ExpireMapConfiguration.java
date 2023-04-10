package io.github.zpf9705.expiring.autoconfigure;

import cn.hutool.aop.proxy.SpringCglibProxyFactory;
import cn.hutool.core.util.ReflectUtil;
import io.github.zpf9705.expiring.banner.ExpireMapBanner;
import io.github.zpf9705.expiring.banner.ExpireStartUpBanner;
import io.github.zpf9705.expiring.banner.StartUpBanner;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfigurationCustomizer;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionFactory;
import io.github.zpf9705.expiring.core.ExpireProperties;
import io.github.zpf9705.expiring.core.logger.Console;
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

/**
 * Expire connection configuration using ExpireMap {@link net.jodah.expiringmap.ExpiringMap}
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

    public ExpireMapConfiguration(ExpireProperties properties) {
        super(properties);
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBanner.printBanner(environment, getStartUpBanner(), sourceClass, out);
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
            List<ExpirationListener> expirationListener = findExpirationListener();
            if (!CollectionUtils.isEmpty(expirationListener)) {
                expirationListener.forEach(builder::addExpiredListener);
            }
        };
    }

    @SuppressWarnings({"rawtypes"})
    public List<ExpirationListener> findExpirationListener() {
        //obtain listing packages path
        String listeningPackages = getProperties().getExpiringMap().getListeningPackages();
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
            try {
                expirationListeners.add(ReflectUtil.newInstance(aClass));
            }catch (Exception ignored){}
        }
        return expirationListeners;
    }
}
