package io.github.expiring.core;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * When set a date, then find the bean in the annotation corresponding value to inform the corresponding
 * key and the value
 * </p>
 * @see net.jodah.expiringmap.ExpirationListener
 *
 *
 * @author zpf
 * @since 1.1.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiringListeners {

    ExpiringListener[] value() default {};
}
