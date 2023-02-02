package com.bookuu.soft.ec.autoconfigure;


import cn.hutool.aop.proxy.SpringCglibProxyFactory;
import cn.hutool.core.util.ReflectUtil;
import com.bookuu.soft.ec.core.*;
import com.google.common.collect.Lists;
import net.jodah.expiringmap.ExpirationListener;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * @see org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.InitializingBean
 * <p>
 * this configuration apply to spring boot 2.5.6
 * The automatic assembly provides the cache configuration , main example
 * ......
 * <string,string> and <string,object>
 * template {@link ExpireTemplate} {@link StringExpiredTemplate}
 * and you can use simple util interface {@link ValueOperations}
 * <p>
 * {@link ConfigurationCustomizer} can implement code configuration level
 * ......
 * {@link ExpireConfigurationCustomizer} can be added dynamically expired listeners
 * you only implementation {@link net.jodah.expiringmap.ExpirationListener}
 * and pay attention to the generic template
 * </p>
 * <p>
 * after you see this introduce
 * To achieve your business
 *
 * @author zpf
 * @since 1.1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnJava(JavaVersion.EIGHT)
@EnableConfigurationProperties(ExpireMapCacheProperties.class)
public class ExpireMapAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private final ExpireMapCacheProperties expireMapCacheProperties;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    private final List<ExpireConfigurationCustomizer<String, Object>> expireConfigurationCustomizersSO;

    private final List<ExpireConfigurationCustomizer<String, String>> expireConfigurationCustomizersSS;

    private ApplicationContext applicationContext;

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
        if (!CollectionUtils.isEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.expireMapCacheProperties));
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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
        if (StringUtils.isBlank(listeningPackages))
            throw new BeanInitializationException(
                    "no provider listening scan path ," +
                            "so ec no can provider binding Expiration Listener !"
            );
        //reflection find packages
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(listeningPackages)
        );
        //reflection find ExpirationListener impl
        Set<Class<? extends ExpirationListener>> subTypesOf =
                reflections.getSubTypesOf(ExpirationListener.class);
        if (CollectionUtils.isEmpty(subTypesOf))
            throw new BeanInitializationException(
                    "no provider implementation ExpirationListener class ," +
                            "so ec no can provider binding Expiration Listener !"
            );
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
                        Console.logger.debug("template bean [{}] bind listener [{}] success",
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
    public String globePersistenceRegain(@Value("${expire.cache.globe-config.persistence.persistence-path:-0}")
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
