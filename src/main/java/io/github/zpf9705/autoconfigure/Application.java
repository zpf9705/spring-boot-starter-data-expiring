package io.github.zpf9705.autoconfigure;

import io.github.zpf9705.listener.ExpiringListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * When to note {@link ExpiringListener} binding to the appropriate template model from the ioc container
 *
 * @author zpf
 * @since 1.1.0
 */
public class Application implements ApplicationContextAware {

    public static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Application.context = applicationContext;
    }
}
