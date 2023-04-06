package io.github.zpf9705.expiring.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You can use this annotation to specify the corresponding template bean add monitor date
 * Since 3.0.0 version no longer provide the technical support
 *
 * @author zpf
 * @since 1.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ExpiringListener {

    /**
     * Here by providing the bean name to find corresponding
     * The corresponding listener binding to the corresponding cache on the template
     *
     * @return bean name
     */
    String expirationForBean() default "";
}
