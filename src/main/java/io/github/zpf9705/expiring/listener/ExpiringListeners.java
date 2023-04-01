package io.github.zpf9705.expiring.listener;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is {@link ExpiringListener} group of annotation mode, can realize the mass of a single template pattern
 *
 * @author zpf
 * @since 1.1.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ExpiringListeners {

    ExpiringListener[] value() default {};
}
