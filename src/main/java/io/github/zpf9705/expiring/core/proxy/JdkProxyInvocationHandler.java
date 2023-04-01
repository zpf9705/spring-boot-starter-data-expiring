package io.github.zpf9705.expiring.core.proxy;

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
public abstract class JdkProxyInvocationHandler<A extends Annotation> implements
        ProxyAnnotationAware<A>,
        InvocationHandler,
        Serializable {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //exec proxy method
        Object invokeResult = method.invoke(args);
        Class<A> proxyAnnotation = getProxyAnnotation();
        //if null return direct invokeResult
        if (proxyAnnotation == null) {
            return invokeResult;
        }
        //get this proxyExec annotation
        A proxyExec = method.getAnnotation(proxyAnnotation);
        if (proxyExec != null) {
            invokeSubsequent(proxyExec, args);
        }
        return invokeResult;
    }

    @Override
    public abstract Class<A> getProxyAnnotation();

    /**
     * Agent performs follow-up unified method
     *
     * @param proxyExec proxy exec annotation
     */
    public abstract void invokeSubsequent(A proxyExec, Object[] args);
}
