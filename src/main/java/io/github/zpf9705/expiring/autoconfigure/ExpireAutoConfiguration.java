package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.banner.ExpireStartUpBanner;
import io.github.zpf9705.expiring.banner.ExpireStarterBanner;
import io.github.zpf9705.expiring.banner.StartUpBanner;
import io.github.zpf9705.expiring.banner.Version;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.core.*;
import io.github.zpf9705.expiring.listener.ExpiringListener;
import io.github.zpf9705.expiring.listener.ExpiringListeners;
import io.github.zpf9705.expiring.core.ExpireGlobePersistence;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializerAdapter;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.PrintStream;
import java.util.List;

/**
 * This autoconfiguration apply to spring boot
 * The automatic assembly provides the cache configuration depends on the class and the realization of the annotation
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * {@link org.springframework.beans.factory.InitializingBean}
 * {@link org.springframework.beans.factory.BeanFactory}
 * {@link EnvironmentAware}
 * {@link ApplicationContextAware}
 * {@link Environment}
 * {@link ApplicationContext}
 * Here has been configured with key/value pair {@code String , String } {@code  String , Object} Template model
 * You can go to see the specific class {@link ExpireTemplate} {@link StringExpireTemplate}
 * At the same time they also good to operation interface type {@link ValueOperations}
 * At the same time you can use {@link ConfigurationCustomizer} to provide personalized configuration expiring map
 * But can be by  {@link ObjectProvider} use an array collection mode faces interface configuration mode
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
@ConditionalOnClass({ExpireOperations.class})
@EnableConfigurationProperties({ExpireProperties.class})
@Import(ExpireMapConfiguration.class)
public class ExpireAutoConfiguration extends AbstractExpireConfiguration implements InitializingBean {

    private final ExpireProperties expireProperties;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    public ExpireAutoConfiguration(ExpireProperties expireProperties,
                                   ObjectProvider<List<ConfigurationCustomizer>> customizerS) {
        this.expireProperties = expireProperties;
        this.configurationCustomizers = customizerS.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.expireProperties));
        }
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire starters version and banner info
         */
        ExpireStartUpBanner.printBanner(environment, getStartUpBanner(), sourceClass, out);
    }

    @Override
    @NonNull
    public Class<?> getSourceClass() {
        return Version.class;
    }

    @Override
    @NonNull
    public StartUpBanner getStartUpBanner() {
        return new ExpireStarterBanner();
    }

    @Bean(name = "keySValueOExpireTemplate")
    @ConditionalOnMissingBean(name = "keySValueOExpireTemplate")
    public ExpireTemplate<String, Object> keySValueOExpireTemplate(ExpireConnectionFactory connectionFactory) {
        ExpireTemplate<String, Object> template = new ExpireTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setFactoryBeanName("keySValueOExpireTemplate");
        template.setKeySerializer(new GenericStringExpiringSerializer());
        template.setValueSerializer(new ExpiringSerializerAdapter<>(Object.class));
        return template;
    }

    @Bean(name = "keySValueSExpiredTemplate")
    @ConditionalOnMissingBean(name = "keySValueSExpiredTemplate")
    public StringExpireTemplate keySValueSExpiredTemplate(ExpireConnectionFactory connectionFactory) {
        StringExpireTemplate template = new StringExpireTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setFactoryBeanName("keySValueSExpiredTemplate");
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
                                                                     StringExpireTemplate template) {
        return template.opsForValue();
    }

    @Bean
    @ConditionalOnBean(name = "keySValueOExpireTemplate")
    @ConditionalOnMissingBean(name = "keySValueOExpirationOperations")
    public ExpirationOperations<String, Object> keySValueOExpirationOperations(@Qualifier("keySValueOExpireTemplate")
                                                                               ExpireTemplate<String, Object> template) {
        return template.opsExpirationOperations();
    }

    @Bean
    @ConditionalOnBean(name = "keySValueSExpiredTemplate")
    @ConditionalOnMissingBean(name = "keySValueSExpirationOperations")
    public ExpirationOperations<String, String> keySValueSExpirationOperations(@Qualifier("keySValueSExpiredTemplate")
                                                                               StringExpireTemplate template) {
        return template.opsExpirationOperations();
    }

    @Bean("application-ec")
    @ConditionalOnMissingBean(name = {"application-ec"})
    public Application application() {
        return new Application();
    }
}
