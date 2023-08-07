package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import java.lang.annotation.*;

/**
 * Dynamic injection of Spring container annotations, which only need to be annotated on the interface class
 * that needs dynamic injection, can be scanned by {@link SdkProxyBeanDefinitionRegister} and automatically
 * create proxy objects based on annotation properties .
 * <p>
 * The class that wears this annotation can be injected and used in the container environment of the spring boot
 * project through {@link org.springframework.beans.factory.annotation.Autowired}, constructors, or set methods
 *
 * @author zpf
 * @since 3.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sdk {

    /**
     * If [spring ioc bean alias] is not filled in, the interface name will be used as the bean name by default
     *
     * @return no be {@literal null}
     */
    String alisa() default "";

    /**
     * [host address configuration key] Support Type :
     * <p>
     * 1、El expression : ${xxx.xxx.xxx}
     * <p>
     * 2、Normal key format : xxx.xxx.xxx
     *
     * @return no be {@literal null}
     */
    String hostProperty();
}
