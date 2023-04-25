package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.core.*;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.persistence.ExpireGlobePersistenceRenewFactory;
import io.github.zpf9705.expiring.core.persistence.PersistenceRenewFactory;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializerAdapter;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.util.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.io.PrintStream;
import java.util.List;

/**
 * This configuration is dependent on the spring autowire mechanism of the boot
 * The principle of the automatic assembly depends on spring mechanism of SPI
 * Specific performance for {@code resources/META-INF/spring.factories}
 * Show the annotation {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * <p>
 * Automatic assembly paradigm for here
 * <pre>
 *        {@code ExpireTemplate<String, String> = new StringExpireTemplate()}
 *        {@code ExpireTemplate<String, Object> = new ExpireTemplate()}
 * </pre>
 * You can go to see the specific class To learn more
 * Also provides Jane in operating interface {@link ValueOperations} to simple operations
 * At the same time you can use {@link ConfigurationCustomizer} to provide personalized
 * configuration expiring , but can be by  {@link ObjectProvider} use an array collection
 * mode faces interface configuration mode
 * <p>
 * Now according to Spring - the boot - starters - data - redis encapsulation mode
 * Open in the form of the client to build Expiring, each is implemented in the client.
 * Such as {@link io.github.zpf9705.expiring.help.expiremap.ExpireMapClientConfiguration}
 * All the operation will be placed on the Helper and simulate the join operation
 * Such as {@link io.github.zpf9705.expiring.help.ExpireHelper} .
 * This layer is {@link net.jodah.expiringmap.ExpiringMap}
 * Additional data on the bottom will adopt a byte type for storage in order to enhance
 * the cache restart recovery
 *
 * @author zpf
 * @since 1.1.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ExpireOperations.class})
@EnableConfigurationProperties({ExpireProperties.class})
@Import(ExpireMapConfiguration.class)
public class ExpireAutoConfiguration implements ExpireBannerDisplayDevice, EnvironmentAware, PersistenceCapable {

    private final ExpireProperties expireProperties;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    public Environment environment;

    public ExpireAutoConfiguration(ExpireProperties expireProperties,
                                   ObjectProvider<List<ConfigurationCustomizer>> customizerS) {
        this.expireProperties = expireProperties;
        this.configurationCustomizers = customizerS.getIfAvailable();
    }

    @Override
    public void afterPropertiesSet() {
        this.printBanner(this.environment, getSourceClass(), System.out);
        if (CollectionUtils.simpleNotEmpty(configurationCustomizers)) {
            configurationCustomizers.forEach(v -> v.customize(this.expireProperties));
        }
    }

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        /*
         * print expire starters version and banner info
         */
        ExpireStartUpBannerExecutor.printBanner(environment, getStartUpBanner(), sourceClass, out);
    }

    @Override
    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    @NotNull
    public Class<?> getSourceClass() {
        return Version.class;
    }

    @Override
    @NotNull
    public StartUpBanner getStartUpBanner() {
        return new ExpireStarterBanner();
    }

    @Bean(DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE)
    public ExpireTemplate<String, Object> expireTemplate(ExpireHelperFactory helperFactory) {
        ExpireTemplate<String, Object> template = new ExpireTemplate<>();
        template.setHelperFactory(helperFactory);
        template.setKeySerializer(new GenericStringExpiringSerializer());
        template.setValueSerializer(new ExpiringSerializerAdapter<>(Object.class));
        return template;
    }

    @Bean(DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE)
    public StringExpireTemplate stringExpireTemplate(ExpireHelperFactory helperFactory) {
        StringExpireTemplate template = new StringExpireTemplate();
        template.setHelperFactory(helperFactory);
        return template;
    }

    @Bean(DEFAULT_SO_TEMPLATE_OPERATION)
    @ConditionalOnBean(name = DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE_OPERATION)
    public ValueOperations<String, Object> valueOperations(@Qualifier(DEFAULT_SO_TEMPLATE)
                                                           ExpireTemplate<String, Object> template) {
        return template.opsForValue();
    }

    @Bean(DEFAULT_SS_TEMPLATE_OPERATION)
    @ConditionalOnBean(name = DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE_OPERATION)
    public ValueOperations<String, String> valueOperations(@Qualifier(DEFAULT_SS_TEMPLATE)
                                                           StringExpireTemplate template) {
        return template.opsForValue();
    }

    @Bean(DEFAULT_SO_TEMPLATE_OPERATION_E)
    @ConditionalOnBean(name = DEFAULT_SO_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SO_TEMPLATE_OPERATION_E)
    public ExpirationOperations<String, Object> expirationOperations(@Qualifier(DEFAULT_SO_TEMPLATE)
                                                                     ExpireTemplate<String, Object> template) {
        return template.opsExpirationOperations();
    }

    @Bean(DEFAULT_SS_TEMPLATE_OPERATION_E)
    @ConditionalOnBean(name = DEFAULT_SS_TEMPLATE)
    @ConditionalOnMissingBean(name = DEFAULT_SS_TEMPLATE_OPERATION_E)
    public ExpirationOperations<String, String> expirationOperations(@Qualifier(DEFAULT_SS_TEMPLATE)
                                                                     StringExpireTemplate template) {
        return template.opsExpirationOperations();
    }

    @Bean("auto::persistenceRegain")
    @Override
    @ConditionalOnProperty(prefix = "spring.data.expiry", name = "open-persistence", havingValue = "true")
    @ConditionalOnBean(ExpireTemplate.class)
    public String persistenceRegain(@Value("${spring.data.expiry.persistence-path:default}") String path) {
        Class<?> factoryClass = this.expireProperties.getPersistenceFactoryClass();
        if (factoryClass == null) {
            return "Open persistence now , but provider factoryClass is null so persistenceRegain failed";
        }
        String clientName = factoryClass.getName();
        PersistenceRenewFactory factory = ExpireGlobePersistenceRenewFactory.getPersistenceFRenewFactory(factoryClass);
        if (factory == null) {
            return "Client name [" + clientName + "] persistenceRegain failed";
        }
        factory.deserializeWithPath(path);
        return "Client name [" + clientName + "] persistenceRegain ok";
    }


    //-----------------------------------Bean name example-------------------------------------
    public static final String DEFAULT_SO_TEMPLATE = "DEFAULT_SO_TEMPLATE";

    public static final String OPERATION = "_OPERATION";

    public static final String OPERATION_E = "_OPERATION_E";

    public static final String DEFAULT_SO_TEMPLATE_OPERATION = DEFAULT_SO_TEMPLATE + OPERATION;

    public static final String DEFAULT_SO_TEMPLATE_OPERATION_E = DEFAULT_SO_TEMPLATE + OPERATION_E;

    public static final String DEFAULT_SS_TEMPLATE = "DEFAULT_SS_TEMPLATE";

    public static final String DEFAULT_SS_TEMPLATE_OPERATION = DEFAULT_SS_TEMPLATE + OPERATION;

    public static final String DEFAULT_SS_TEMPLATE_OPERATION_E = DEFAULT_SS_TEMPLATE + OPERATION_E;
}
