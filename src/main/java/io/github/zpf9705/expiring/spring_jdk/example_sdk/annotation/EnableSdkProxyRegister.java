package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * This annotation is mainly annotated on the class where the Spring injection class annotation is located, which
 * can implement the interface class with automatic injection annotation {@link Sdk} and automatically create
 * the implementation class,it mainly relies on {@link SdkProxyBeanDefinitionRegister}
 * <p>
 * @see io.github.zpf9705.expiring.spring_jdk.support.AbstractProxyBeanInjectSupport
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.core.env.Environment
 * @author zpf
 * @since 3.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SdkProxyBeanDefinitionRegister.class})
public @interface EnableSdkProxyRegister {

    /**
     * Carrying the path where the {@link Sdk} class is located
     *
     * @return alias for {{@link #basePackages()}}
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it
     *
     * @return alias for {{@link #value()}}
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
