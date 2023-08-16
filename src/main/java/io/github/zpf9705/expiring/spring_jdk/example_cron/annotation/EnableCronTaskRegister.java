package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
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
     * @return alias for {{@link #basePackages()}} If empty, the entire path will be scanned
     */
    @AliasFor("basePackages")
    String[] value() default {"io", "com", "cn"};

    /**
     * His value shifts to {@link #value()}, consistent with it
     *
     * @return alias for {{@link #value()}} If empty, the entire path will be scanned
     */
    @AliasFor("value")
    String[] basePackages() default {"io", "com", "cn"};

    /**
     * The survival mode of the calling object triggered by a timed task,
     * which defaults to the proxy object of the spring container
     *
     * @return {@link Mode}
     */
    Mode mode() default Mode.PROXY;
}
