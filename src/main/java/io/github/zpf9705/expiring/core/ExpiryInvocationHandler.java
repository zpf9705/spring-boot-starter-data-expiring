package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The proxy class of {@code Expiry} handles abstract classes, and the unified processing of abstract
 * parameters diverges from the center. The premise is that the implementation is based on annotation
 * information, which is combined with actual parameters to handle such a logic.
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ExpiryInvocationHandler<T, A extends Annotation> implements InvocationHandler, Serializable {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //source target object
        final T target = getTarget();
        //exec proxy method
        Object invokeResult = method.invoke(target, args);
        //get this proxyExec annotation
        A proxyExec = method.getAnnotation(getAppointAnnotationClazz());
        if (proxyExec != null) {
            invokeSubsequent(invokeResult, proxyExec, args);
        }
        return invokeResult;
    }

    @NotNull
    public abstract Class<A> getAppointAnnotationClazz();

    /**
     * Get Execution Object
     *
     * @return Not be {@literal null}
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
