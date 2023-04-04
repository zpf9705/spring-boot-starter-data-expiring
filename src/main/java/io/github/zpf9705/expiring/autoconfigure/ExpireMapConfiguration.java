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
import io.github.zpf9705.expiring.core.ValueOperations;
import io.github.zpf9705.expiring.core.persistence.ExpireGlobePersistenceFactory;
import io.github.zpf9705.expiring.core.persistence.ExpireSimpleGlobePersistence;
import io.github.zpf9705.expiring.core.persistence.PersistenceFactory;
import io.github.zpf9705.expiring.listener.PersistenceExpiringCallback;
import io.github.zpf9705.expiring.core.logger.Console;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
public class ExpireMapConfiguration extends AbstractExpireConfiguration implements InitializingBean {

    private final ExpireProperties expireProperties;

    public ExpireMapConfiguration(ExpireProperties expireProperties) {
        this.expireProperties = expireProperties;
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBanner.printBanner(environment, getStartUpBanner(), sourceClass, out);
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
        ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder
                = ExpireMapClientConfiguration.builder();
        buildCustomizer.orderedStream()
                .forEach((customizer) -> customizer.customize(builder));
        return new ExpireMapConnectionFactory(builder.build());
    }

    @Bean("expireMap::persistenceRegain")
    @Override
    @ConditionalOnProperty(prefix = "expire.config", name = "open-persistence", havingValue = "true")
    @ConditionalOnBean(value = {ValueOperations.class}, name = {"application-ec"})
    public String persistenceRegain(@Value("${expire.config.persistence-path:default}") String path) {
        PersistenceFactory factory = ExpireGlobePersistenceFactory.getPersistenceFactory(
                ExpireSimpleGlobePersistence.class.getName());
        if (factory == null) {
            return "Client name [" + ExpiringMap.class.getName() + "] persistenceRegain failed";
        }
        factory.deserializeWithPath(path);
        return "Client name [" + ExpiringMap.class.getName() + "] persistenceRegain ok";
    }

    @Bean("expireMap::expireMapClientCustomizer")
    @SuppressWarnings("rawtypes")
    public ExpireMapClientConfigurationCustomizer expireMapClientCustomizer() {
        return c -> {
            ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder builder =
                    c.acquireMaxSize(this.expireProperties.getExpiringMap().getMaxSize())
                            .acquireDefaultExpireTime(this.expireProperties.getDefaultExpireTime())
                            .acquireDefaultExpireTimeUnit(this.expireProperties.getDefaultExpireTimeUnit())
                            .acquireDefaultExpirationPolicy(this.expireProperties.getExpiringMap().getExpirationPolicy());
            List<ExpirationListener> expirationListener = findExpirationListener();
            if (!CollectionUtils.isEmpty(expirationListener)) {
                expirationListener.forEach(builder::addExpiredListener);
            }
        };
    }

    @SuppressWarnings({"rawtypes"})
    public List<ExpirationListener> findExpirationListener() {
        //obtain listing packages path
        String listeningPackages = this.expireProperties.getExpiringMap().getListeningPackages();
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
