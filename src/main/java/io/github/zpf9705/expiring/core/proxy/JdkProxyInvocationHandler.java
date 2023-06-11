package io.github.zpf9705.expiring.core.proxy;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The JDK agent united actuators {@link InvocationHandler#invoke(Object, Method, Object[])}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class JdkProxyInvocationHandler<T, A extends Annotation> implements
        ProxyAnnotationAware<A>,
        InvocationHandler,
        Serializable {

    private static final long serialVersionUID = 12312323L;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //source target object
        final T target = getTarget();
        //exec proxy method
        Object invokeResult = method.invoke(target, args);
        //get this proxyExec annotation
        A proxyExec = method.getAnnotation(getProxyAnnotation());
        if (proxyExec != null) {
            invokeSubsequent(invokeResult, proxyExec, args);
        }
        return invokeResult;
    }

    @Override
    @NotNull
    public abstract Class<A> getProxyAnnotation();

    /**
     * Get target object
     *
     * @return target object
     */
    @NotNull
    public abstract T getTarget();

    /**
     * Agent performs follow-up unified method
     *
     * @param invokeResult exec result
     * @param proxyExec    proxy exec annotation
     * @param args         proxy method args
     */
    public abstract void invokeSubsequent(@CanNull Object invokeResult, A proxyExec, Object[] args);
}
