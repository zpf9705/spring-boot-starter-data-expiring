package io.github.zpf9705.expiring.spring_jdk.example_cron;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Start the registration timer task in the form of each object being retrieved from the Spring container,
 * and the object of the registration method needs to be added to the Spring container
 *
 * @author zpf
 * @since 3.1.5
 */
public class CronWithBeanCallRegister extends AbstractCornRegister implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    @NotNull
    public Consumer<Method> registersConsumer() {
        return (method) -> {
            Object proxy;
            try {
                proxy = applicationContext.getBean(method.getDeclaringClass());
            } catch (Exception e) {
                Console.info("Load [{}] bean error : [{}]", method.getDeclaringClass(), e.getMessage());
                proxy = null;
            }
            CronRegister.register(proxy, method);
        };
    }
}
