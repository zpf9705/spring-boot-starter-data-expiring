package io.github.zpf9705.expiring.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * By {@link ApplicationContextAware} to the spring context,
 * in the injection classes under the environment of use
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

    /*
     * (non-javadoc)
     * @see org.springframework.context.ApplicationContext#getBean(String)
     */
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

    /*
     * (non-javadoc)
     * @see org.springframework.context.ApplicationContext#getBean(Class)
     */
    public static <T> T findBean(Class<T> beanClass) {
        if (beanClass == null) {
            return null;
        }
        T bean;
        try {
            bean = context.getBean(beanClass);
        } catch (Throwable e) {
            bean = null;
        }
        return bean;
    }

    /*
     * (non-javadoc)
     * @see org.springframework.context.ApplicationContext#getBeansOfType(Class)
     */
    public static <T> Map<String, T> findAnyBean(Class<T> beanClass) {
        if (beanClass == null) {
            return null;
        }
        Map<String,T> beanMap;
        try {
            beanMap = context.getBeansOfType(beanClass);
        }catch (Throwable e){
            beanMap = Collections.emptyMap();
        }
        return beanMap;
    }
}
