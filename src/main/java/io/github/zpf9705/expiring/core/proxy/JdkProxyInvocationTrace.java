package io.github.zpf9705.expiring.core.proxy;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.lang.annotation.Annotation;

/**
 * Interfaces for Jdk Proxy Invocation Trace
 *
 * @author zpf
 * @since 3.0.0
 */
public class JdkProxyInvocationTrace<T, A extends Annotation> extends JdkProxyInvocationHandler<T, A> {

    private static final long serialVersionUID = 1220499099081639297L;

    private final T target;

    private final Class<A> annotationClass;

    public JdkProxyInvocationTrace(T target, Class<A> annotationClass) {
        this.target = target;
        this.annotationClass = annotationClass;
    }

    @Override
    @NotNull
    public Class<A> getProxyAnnotation() {
        return this.annotationClass;
    }

    @Override
    @NotNull
    public T getTarget() {
        return this.target;
    }

    @Override
    public void invokeSubsequent(Object invokeResult, A proxyExec, Object[] args) {
        AssertUtils.Persistence.notNull(proxyExec, "PersistenceExec no be null");
        AssertUtils.Persistence.notEmpty(args, "Args no be null");
    }
}
