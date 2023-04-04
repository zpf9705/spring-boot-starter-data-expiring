package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.banner.ExpireStartUpBanner;
import io.github.zpf9705.expiring.banner.ExpireStarterBanner;
import io.github.zpf9705.expiring.banner.StartUpBanner;
import io.github.zpf9705.expiring.banner.Version;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.core.*;
import io.github.zpf9705.expiring.core.persistence.ExpireGlobePersistenceFactory;
import io.github.zpf9705.expiring.core.persistence.PersistenceFactory;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializerAdapter;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
 * Here has been configured with key/value pair {@code String , String } {@code  String , Object} Template model
 * You can go to see the specific class {@link ExpireTemplate} {@link StringExpireTemplate}
 * At the same time they also good to operation interface type {@link ValueOperations}
 * At the same time you can use {@link ConfigurationCustomizer} to provide personalized configuration expiring
 * But can be by  {@link ObjectProvider} use an array collection mode faces interface configuration mode
 * Now according to Spring - the boot - starters - data - redis encapsulation mode
 * Open in the form of the client to build Expiring, each is implemented in the client.
 * Such as {@link io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration}
 * All the operation will be placed on the Connection and simulate the join operation
 * Such as {@link io.github.zpf9705.expiring.connection.ExpireConnection} . This layer is {@link net.jodah.expiringmap.ExpiringMap}
 * Additional data on the bottom will adopt a byte type for storage in order to enhance the cache restart recovery
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
        super.afterPropertiesSet();
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

    @Bean("expireMap::persistenceRegain")
    @Override
    @ConditionalOnProperty(prefix = "expire.config", name = "open-persistence", havingValue = "true")
    @ConditionalOnBean(value = {ValueOperations.class}, name = {"application-ec"})
    public String persistenceRegain(@Value("${expire.config.persistence-path:default}") String path) {
        Class<?> factoryClass = this.expireProperties.getPersistenceFactoryClass();
        if (factoryClass == null) {
            return "Open persistence now , but provider factoryClass is null so persistenceRegain failed";
        }
        String clientName = factoryClass.getName();
        PersistenceFactory factory = ExpireGlobePersistenceFactory.getPersistenceFactory(clientName);
        if (factory == null) {
            return "Client name [" + clientName + "] persistenceRegain failed";
        }
        factory.deserializeWithPath(path);
        return "Client name [" + clientName + "] persistenceRegain ok";
    }
}
