package io.github.zpf9705.expiring.spring_jdk.example_cron;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.support.SupportException;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Start the scheduled task of registration in the form of instantiation of each object,
 * and the object of the registration method needs to meet the null parameter construction {@link Class#newInstance()}
 *
 * @author zpf
 * @since 3.1.5
 */
@Configuration(proxyBeanMethods = false)
public class CronWithInstanceCallRegister extends AbstractCornRegister {

    @Override
    public void register(@NotNull List<Method> filterMethods) {
        for (Method method : filterMethods) {
            Object instance;
            try {
                instance = method.getDeclaringClass().newInstance();
            } catch (InstantiationException e) {
                throw new SupportException("Class name {" + method.getDeclaringClass().getName() + "} not " +
                        "found empty parameter construct cannot be instantiated");
            } catch (IllegalAccessException e) {
                throw new SupportException("Class name {" + method.getDeclaringClass().getName() + "} does " +
                        "not have permission to use. Please check the permission modifier so that we can use this class");
            }
            singleRegister(instance, method);
        }
        start();
    }
}
