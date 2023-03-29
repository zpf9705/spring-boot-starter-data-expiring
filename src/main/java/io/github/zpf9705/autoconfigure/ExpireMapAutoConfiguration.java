package io.github.zpf9705.autoconfigure;


import cn.hutool.aop.proxy.SpringCglibProxyFactory;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import io.github.zpf9705.banner.ExpireStartUpBanner;
import io.github.zpf9705.core.*;
import io.github.zpf9705.listener.ExpiringListener;
import io.github.zpf9705.listener.ExpiringListeners;
import io.github.zpf9705.logger.Console;
import io.github.zpf9705.persistence.ExpireGlobePersistence;
import io.github.zpf9705.serializer.ExpiringSerializerAdapter;
import io.github.zpf9705.serializer.GenericStringExpiringSerializer;
import net.jodah.expiringmap.ExpirationListener;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This auto configuration apply to spring boot
 * The automatic assembly provides the cache configuration depends on the class and the realization of the annotation
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * {@link org.springframework.beans.factory.InitializingBean}
 * {@link org.springframework.beans.factory.BeanFactory}
 * {@link EnvironmentAware}
 * {@link ApplicationContextAware}
 * {@link Environment}
 * {@link ApplicationContext}
 * Here has been configured with key/value pair {@code String , String } {@code  String , Object} Template model
 * You can go to see the specific class {@link ExpireTemplate} {@link StringExpiredTemplate}
 * At the same time they also good to operation interface type {@link ValueOperations}
 * At the same time you can use {@link ConfigurationCustomizer} to provide personalized configuration expiring map
 * But can be by {@link ExpireConfigurationCustomizer} {@link ObjectProvider} use an array collection mode faces interface configuration mode
 * At the same time provides a cache expiration monitoring function , you can see {@link ExpiringListener}
 * or {@link ExpiringListeners} and you only implementation {@link net.jodah.expiringmap.ExpirationListener}
 * The corresponding annotation on the class, can help you find the corresponding generic template,
 * do it after the expiration of correction in a timely manner
 * After you see this introduce to achieve your business
 *
 * @author zpf
 * @since 1.1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnJava(JavaVersion.EIGHT)
@EnableConfigurationProperties(ExpireMapCacheProperties.class)
public class ExpireMapAutoConfiguration implements InitializingBean, ApplicationContextAware, EnvironmentAware {

    private final ExpireMapCacheProperties expireMapCacheProperties;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final List<ExpireConfigurationCustomizer<String, Object>> expireConfigurationCustomizersSO;

    private final List<ExpireConfigurationCustomizer<String, String>> expireConfigurationCustomizersSS;

    private ApplicationContext applicationContext;

    private Environment environment;

    public ExpireMapAutoConfiguration(ExpireMapCacheProperties expireMapCacheProperties,
                                      ObjectProvider<List<ConfigurationCustomizer>> customizerS,
                                      ObjectProvider<List<ExpireConfigurationCustomizer<String, Object>>> customizerSO,
                                      ObjectProvider<List<ExpireConfigurationCustomizer<String, String>>> customizerSS) {
        this.expireMapCacheProperties = expireMapCacheProperties;
        this.configurationCustomizers = customizerS.getIfAvailable();
        this.expireConfigurationCustomizersSO = customizerSO.getIfAvailable();
        this.expireConfigurationCustomizersSS = customizerSS.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        /*
         * print expire - map version and banner info
         */
        ExpireStartUpBanner.bannerPrinter(environment, ExpireMapAutoConfiguration.class);
        if (!CollectionUtils.isEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.expireMapCacheProperties));
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "keySValueOExpireTemplate")
    @ConditionalOnMissingBean(name = "keySValueOExpireTemplate")
    public ExpireTemplate<String, Object> keySValueOExpireTemplate() {
        ExpireTemplate<String, Object> template =
                new ExpireTemplate<>("keySValueOExpireTemplate");
        template.setKeySerializer(new GenericStringExpiringSerializer());
        template.setValueSerializer(new ExpiringSerializerAdapter<>(Object.class));
        customizerOfConfiguration(template, this.expireConfigurationCustomizersSO);
        return template;
    }

    @Bean(name = "keySValueSExpiredTemplate")
    @ConditionalOnMissingBean(name = "keySValueSExpiredTemplate")
    public StringExpiredTemplate keySValueSExpiredTemplate() {
        StringExpiredTemplate template =
                new StringExpiredTemplate("keySValueSExpiredTemplate");
        customizerOfConfiguration(template, this.expireConfigurationCustomizersSS);
        return template;
    }

    @Bean
    @ConditionalOnBean(name = "keySValueOExpireTemplate")
    @ConditionalOnMissingBean(name = "keySValueOValueOperations")
    public ValueOperations<String, Object> keySValueOValueOperations(@Qualifier("keySValueOExpireTemplate")
                                                                     ExpireTemplate<String, Object> template) {
        return template.opsForValue();
    }

    @Bean
    @ConditionalOnBean(name = "keySValueSExpiredTemplate")
    @ConditionalOnMissingBean(name = "keySValueSValueOperations")
    public ValueOperations<String, String> keySValueSValueOperations(@Qualifier("keySValueSExpiredTemplate")
                                                                     StringExpiredTemplate template) {
        return template.opsForValue();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ConditionalOnBean(ExpireTemplate.class)
    @Bean
    public Object bindingExpirationListener() {
        //obtain listing packages path
        String listeningPackages = expireMapCacheProperties.getListeningPackages();
        if (StringUtils.isBlank(listeningPackages)) {
            Console.getLogger().info(
                    "no provider listening scan path ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return "bind no";
        }
        //reflection find packages
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(listeningPackages)
        );
        //reflection find ExpiringLoadListener impl
        Set<Class<? extends ExpirationListener>> subTypesOf =
                reflections.getSubTypesOf(ExpirationListener.class);
        if (CollectionUtils.isEmpty(subTypesOf)) {
            Console.getLogger().info(
                    "no provider implementation ExpiringLoadListener class ," +
                            "so ec no can provider binding Expiration Listener !"
            );
            return "bind no";
        }
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
            List<String> beanNames = null;
            ExpiringListeners listeners = aClass.getAnnotation(ExpiringListeners.class);
            if (listeners == null) {
                ExpiringListener listener = aClass.getAnnotation(ExpiringListener.class);
                if (listener != null) {
                    beanNames = Lists.newArrayList(listener.expirationForBean());
                }
            } else {
                beanNames = Arrays.stream(listeners.value())
                        .map(ExpiringListener::expirationForBean)
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(beanNames)) {
                continue;
            }

            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                if (!(bean instanceof ExpireTemplate)) {
                    continue;
                }
                ExpireTemplate template = (ExpireTemplate) bean;
                Class<?>[] parameterTypes = target.getParameterTypes();
                if (parameterTypes.length < 2) {
                    continue;
                }
                if (template.getKeySerializer().serializerType() == parameterTypes[0]
                        && template.getValueSerializer().serializerType() == parameterTypes[1]) {
                    ExpirationListener listener =
                            new SpringCglibProxyFactory().proxy(ReflectUtil.newInstance(aClass),
                                    PersistenceExpiringCallback.class);
                    if (listener != null) {
                        template.addExpiredListener(listener);
                        Console.getLogger().info("Template bean [{}] bind listener [{}] success",
                                beanName,
                                aClass.getName());
                    }
                }
            }
        }
        return "binding Expiration Listener ok ...";
    }

    @Bean("application-ec")
    @ConditionalOnMissingBean(name = {"application-ec"})
    public Application application() {
        return new Application();
    }

    @Bean
    @ConditionalOnProperty(prefix = "expire.cache.globe-config.persistence", name = "open-persistence",
            havingValue = "true")
    @ConditionalOnBean(value = {ValueOperations.class}, name = {"application-ec"})
    public String globePersistenceRegain(@Value("${expire.cache.globe-config.persistence.persistence-path:default}")
                                         String path) {
        ExpireGlobePersistence.INSTANCE.deserialize(path);
        return "globe Persistence regain ok";
    }

    public <T extends ExpireTemplate<X, V>, X, V> void customizerOfConfiguration(T t,
                                                                                 List<ExpireConfigurationCustomizer<X, V>> customizers) {
        if (t == null) {
            return;
        }
        // Configure basic configuration
        acquireCacheConfiguration(t);
        // Configure listener configuration
        customizerOfListenerOrOther(t, customizers);
    }

    public <K, V> void acquireCacheConfiguration(ExpireTemplate<K, V> template) {
        if (template == null) return;
        template.checkExpiringMapNoInit();
        template.acquireMaxSize(this.expireMapCacheProperties.getMaxSize())
                .acquireDefaultExpireTime(this.expireMapCacheProperties.getDefaultExpireTime())
                .acquireDefaultExpireTimeTime(this.expireMapCacheProperties.getDefaultExpireTimeUnit())
                .acquireDefaultExpirationPolicy(this.expireMapCacheProperties.getExpirationPolicy())
                .build();
    }

    public <T extends ExpireTemplate<X, V>, X, V> void customizerOfListenerOrOther(T t,
                                                                                   List<ExpireConfigurationCustomizer<X, V>> customizers) {
        if (t != null && !CollectionUtils.isEmpty(customizers)) {
            //Loop configuration c
            customizers.forEach(c0 -> c0.customize(t));
        }
    }
}
