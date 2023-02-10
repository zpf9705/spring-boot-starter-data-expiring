package io.github.expiring.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * <p>
 *   spring application for Bean Factory
 * <p>
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
