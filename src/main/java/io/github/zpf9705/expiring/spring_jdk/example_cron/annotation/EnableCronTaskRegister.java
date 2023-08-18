package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * The annotation identifier of the timed task registration switch can specify
 * the path for scanning {@link Cron} and set the survival mode of the calling
 * object {@link Type}. This annotation will only take effect when it exists on
 * the class header of the spring container object. Please pay attention to how to use it
 * <p>
 * @see CronTaskRegister
 * @see io.github.zpf9705.expiring.spring_jdk.example_cron.AbstractCornRegister
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.core.env.Environment
 * @author zpf
 * @since 3.1.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegister.class})
public @interface EnableCronTaskRegister {

    /**
     * Carrying the path where the {@link Cron} class is located
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

    /**
     * The survival mode of the calling object triggered by a timed task,
     * which defaults to the proxy object of the spring container
     *
     * @return {@link Type}
     */
    Type type() default Type.PROXY;
}
