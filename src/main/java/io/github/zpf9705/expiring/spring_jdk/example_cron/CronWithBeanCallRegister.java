package io.github.zpf9705.expiring.spring_jdk.example_cron;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.support.SupportException;
import io.github.zpf9705.expiring.util.rxjava.SpectatorUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Start the registration timer task in the form of each object being retrieved from the Spring container,
 * and the object of the registration method needs to be added to the Spring container
 * <p>
 * There is a problem like this and a solution :
 * <p>
 * The query of bean objects in the spring proxy container adopts the method of loading failure
 * and reloading at intervals due to loading sequence issues. However, this method will not continue
 * to retry because the proxy method {@link io.github.zpf9705.expiring.spring_jdk.example_cron.annotation.Type}
 * does not include the calling object in the spring container, which also needs to be compatible.
 * Therefore, the maximum number of attempts to search for a single bean is proposed to be 10, with an
 * interval of 1 second each time, We also hope that users can adjust the loading order of proxy object beans
 * in proxy mode to the top.
 *
 * @author zpf
 * @since 3.1.5
 */
public class CronWithBeanCallRegister extends AbstractCornRegister {

    @Override
    public void register(@NotNull List<Method> filterMethods) {
        new Thread(() -> {
            filterMethods.forEach(method -> {
                Object proxy;
                try {
                    proxy = SpectatorUtils.runWhileTrampolineNoConvert(
                            () -> getApplicationContext().getBean(method.getDeclaringClass()),
                            null,
                            10,
                            1000);
                } catch (BeansException e) {
                    throw new SupportException("No beans corresponding to {" + method.getDeclaringClass() + "} " +
                            "were found in the spring container");
                }
                singleRegister(proxy, method);
            });
            start();
        }).start();
    }
}
