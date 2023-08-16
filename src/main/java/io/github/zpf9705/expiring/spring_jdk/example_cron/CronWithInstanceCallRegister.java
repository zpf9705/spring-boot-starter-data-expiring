package io.github.zpf9705.expiring.spring_jdk.example_cron;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Start the scheduled task of registration in the form of instantiation of each object,
 * and the object of the registration method needs to meet the null parameter construction {@link Class#newInstance()}
 *
 * @author zpf
 * @since 3.1.5
 */
public class CronWithInstanceCallRegister extends AbstractCornRegister {

    @Override
    @NotNull
    public Consumer<Method> registersConsumer() {
        return (method) -> {
            Object instance;
            try {
                instance = method.getDeclaringClass().newInstance();
            } catch (Exception e) {
                Console.info("Instance class [{}] error : [{}]", method.getDeclaringClass(), e.getMessage());
                instance = null;
            }
            CronRegister.register(instance, method);
        };
    }
}
