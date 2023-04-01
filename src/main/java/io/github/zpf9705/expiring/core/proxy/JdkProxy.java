package io.github.zpf9705.expiring.core.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * The JDK proxy object generation solution method
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class JdkProxy {

    /**
     * Create target proxy object within an annotation sign
     *
     * @param target          target object
     * @param annotationClass annotation class type
     * @return proxy object
     */
    public static <T, A extends Annotation> T createProxy(T target, Class<A> annotationClass) {
        return createProxy(new JdkProxyInvocationTrace<>(target, annotationClass));
    }

    /**
     * Create target proxy object within an {@code JdkProxyInvocationHandler}
     *
     * @param handler {@link JdkProxyInvocationHandler}
     * @return proxy object
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Annotation> T createProxy(JdkProxyInvocationHandler<T, A> handler) {
        return (T) Proxy.newProxyInstance(getClassLoader(), getInterfaces(handler.getTarget()), handler);
    }

    /**
     * Get class load
     *
     * @return {@link ClassLoader}
     */
    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = JdkProxy.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * Get target class all interfaces
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
