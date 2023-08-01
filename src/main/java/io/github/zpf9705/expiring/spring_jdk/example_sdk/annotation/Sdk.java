package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import java.lang.annotation.*;

/**
 * SDK identification annotation
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
