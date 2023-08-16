package io.github.zpf9705.expiring.spring_jdk.example_cron;


import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.example_cron.annotation.Cron;
import io.github.zpf9705.expiring.spring_jdk.example_cron.annotation.CronTaskRegister;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Abstract registration class, which includes the initial registration and runtime
 * of the timing method. By determining the current startup environment of Spring,
 * the specified environment can be started (directly registering without a specified environment).
 * <p>
 * At the beginning, parameter preparation is carried out, and after the program is started,
 * {@link ApplicationRunner#run(ApplicationArguments)} is used for loading registration.
 * <p>
 * The main purpose is to prevent loading order issues with container beans.
 *
 * @author zpf
 * @since 3.1.5
 */
public abstract class AbstractCornRegister implements InitializingBean, ApplicationRunner, EnvironmentAware {

    private final List<String> profiles = new ArrayList<>();

    private Set<Method> methods;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
    }

    @Override
    public void afterPropertiesSet() {
        String[] scanPackage = CronTaskRegister.getScanPackage();
        if (ArrayUtils.simpleIsEmpty(scanPackage)) {
            Console.info("No scanned package paths found");
            return;
        }
        Set<Method> methods = CronRegister.getScanMethodsWithAnnotation(scanPackage);
        if (CollectionUtils.simpleIsEmpty(methods)) {
            Console.info("No method for annotating @Cron was found");
            return;
        }
        //Filter according to the inclusion of the environment
        this.methods = methods.stream().filter(
                m -> {
                    Cron cron = m.getAnnotation(Cron.class);
                    if (ArrayUtils.simpleNotEmpty(cron.profiles())) {
                        return Arrays.stream(cron.profiles()).anyMatch(profiles::contains);
                    }
                    //no profile args direct register
                    return true;
                }
        ).collect(Collectors.toSet());
        if (CollectionUtils.simpleIsEmpty(methods)) {
            Console.info("There is no standardized method for registering scheduled tasks");
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (CollectionUtils.simpleIsEmpty(this.methods)) {
            return;
        }
        this.methods.forEach(method -> registersConsumer().accept(method));
        CronRegister.start(true, false);
    }

    @NotNull
    public abstract Consumer<Method> registersConsumer();
}
