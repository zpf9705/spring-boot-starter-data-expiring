package io.github.expiring.core;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     You can use this annotation to specify the corresponding template bean add monitor date
 * </p>
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
     * @see ExpireTemplate
     * @see org.springframework.context.ApplicationContext#getBean(String)
     * @return bean name
     */
    String expirationForBean() default "";
}
