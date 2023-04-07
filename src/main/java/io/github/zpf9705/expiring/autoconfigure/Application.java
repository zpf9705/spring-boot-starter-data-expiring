package io.github.zpf9705.expiring.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * Getting late spring context to cache recovery template generic method calls provide lookup
 *
 * @author zpf
 * @since 1.1.0
 */
public class Application implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Application.context = applicationContext;
    }

    public static Object findBean(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            return null;
        }
        Object bean;
        try {
            bean = context.getBean(beanName);
        } catch (Throwable e) {
            bean = null;
        }
        return bean;
    }

    public static <T> T findBean(Class<T> beanClass) {
        if (beanClass == null) {
            return null;
        }
        T properties;
        try {
            properties = context.getBean(beanClass);
        } catch (Throwable e) {
            properties = null;
        }
        return properties;
    }
}
