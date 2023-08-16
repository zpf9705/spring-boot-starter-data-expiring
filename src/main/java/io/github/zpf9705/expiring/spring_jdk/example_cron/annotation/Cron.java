package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Flag annotation for timed task method registration, only supports starting with cron expression,
 * supports spring specified environment {@link org.springframework.core.env.Environment}registration,
 * and provides fixed initialization parameters
 * <p>
 * Relying on {@link CronTaskRegister} to scan, obtain, and register corresponding annotation methods
 *
 * @author zpf
 * @since 3.1.5
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * The turning direction of the expression value of CORN {@code value}
     *
     * @return corn express
     */
    @AliasFor("express")
    String value();

    /**
     * The turning direction of the expression value of CORN {@code express}
     *
     * @return corn express
     */
    @AliasFor("value")
    String express();

    /**
     * Task method initialization parameters
     *
     * @return can be {@literal null}
     */
    String[] args() default {};

    /**
     * Limited spring startup environment parameters,
     * only when registering the task in the specified environment
     *
     * @return If left blank, register directly
     */
    String[] profiles() default {};
}
