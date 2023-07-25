package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.SdkProxyBeanDefinition;
import io.github.zpf9705.expiring.spring_jdk.support.AbstractProxyBeanInjectSupport;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

/**
 * SDK Interface Proxy Registration
 *
 * @author zpf
 * @since 3.1.0
 */
public class SdkProxyBeanDefinitionRegister extends AbstractProxyBeanInjectSupport<EnableSdkProxyRegister, Sdk> {

    @Override
    @NonNull
    public Class<EnableSdkProxyRegister> getOpenClazz() {
        return EnableSdkProxyRegister.class;
    }

    @Override
    @NonNull
    public Class<Sdk> getFindClazz() {
        return Sdk.class;
    }

    @Override
    public void beanRegister(AnnotationAttributes attributes, BeanDefinitionRegistry registry, AnnotationMetadata meta) {
        String className = meta.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(SdkProxyBeanDefinition.class);
        definition.addPropertyValue("host", getRequestHost(attributes.getString("hostProperty")));
        definition.addPropertyValue("clazz", className);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition.getBeanDefinition(),
                className, new String[]{attributes.getString("alisa")});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    @Override
    @NonNull
    public String getPackagesSign() {
        return "basePackages";
    }

    /**
     * Obtain host configuration parameters,According to the startup environment of spring
     *
     * @param hostProperty Host configuration key
     * @return host address
     */
    private String getRequestHost(String hostProperty) {
        AssertUtils.Operation.hasText(hostProperty, "HostProperty no be null");
        String host = getEnvironment().resolvePlaceholders(hostProperty);
        if (StringUtils.simpleIsBlank(host)) {
            host = getEnvironment().getProperty(hostProperty);
        }
        AssertUtils.Operation.hasText(host, "Provided by the configuration keys [" + hostProperty + "] ," +
                "Didn't find the corresponding configuration items , Please check");
        return host;
    }
}
