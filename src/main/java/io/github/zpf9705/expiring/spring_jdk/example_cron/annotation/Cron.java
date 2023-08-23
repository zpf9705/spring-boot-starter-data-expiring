package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

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
     * Fill in a planned cron expression that represents the frequency of task execution
     *
     * @return corn express , must no be {@literal null}
     */
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
