package io.github.zpf9705.core;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You can use this annotation to specify the corresponding template bean add monitor date
 *
 * @author zpf
 * @since 1.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiringListener {

    /**
     * Here by providing the bean name to find corresponding
     * The corresponding listener binding to the corresponding cache on the template
     *
     * @return bean name
     * @see ExpireTemplate
     * @see org.springframework.context.ApplicationContext#getBean(String)
     */
    String expirationForBean() default "";
}
