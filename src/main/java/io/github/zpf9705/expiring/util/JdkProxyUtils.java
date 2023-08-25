package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.ExpiryInvocationHandler;
import io.github.zpf9705.expiring.core.ExpiryInvocationTrace;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * JDK dynamic proxy tool class, reference {@link java.lang.reflect.InvocationHandler},
 * this tool is only intended for use in this project, and there is currently no intention of expanding external use
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class JdkProxyUtils {

    /**
     * Create target proxy object within an annotation sign
     *
     * @param target          target object
     * @param annotationClass annotation class type
     * @param <A>             target type
     * @param <T>             bind annotation after handler
     * @return proxy object
     */
    public static <T, A extends Annotation> T createProxy(T target, Class<A> annotationClass) {
        return createProxy(new ExpiryInvocationTrace<>(target, annotationClass));
    }

    /**
     * Create target proxy object within an {@code JdkProxyInvocationHandler}
     *
     * @param handler {@link ExpiryInvocationHandler}
     * @param <A>     target type
     * @param <T>     bind annotation after handler
     * @return proxy object
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Annotation> T createProxy(ExpiryInvocationHandler<T, A> handler) {
        return (T) Proxy.newProxyInstance(getClassLoader(), getInterfaces(handler.getTarget()), handler);
    }

    /**
     * To obtain a class loader, the thread first obtains it, followed by this class loader
     *
     * @return {@link ClassLoader}
     */
    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = JdkProxyUtils.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * Obtain all implementation interfaces of an instantiated object
     *
     * @param target target object
     * @return interfaces
     */
    private static <T> Class<?>[] getInterfaces(T target) {
        if (target == null) {
            return null;
        }
        return target.getClass().getInterfaces();
    }
}
