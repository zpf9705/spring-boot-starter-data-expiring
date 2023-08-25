package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.lang.annotation.Annotation;

/**
 * The Expiry proxy method executes the real object acquisition processing class,
 * and when the proxy object is called, the real parameters will be transmitted through
 * the class {@link ExpiryInvocationHandler} for real processing here.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpiryInvocationTrace<T, A extends Annotation> extends ExpiryInvocationHandler<T, A> {

    private static final long serialVersionUID = 1220499099081639297L;

    private final T target;

    private final Class<A> annotationClass;

    public ExpiryInvocationTrace(T target, Class<A> annotationClass) {
        this.target = target;
        this.annotationClass = annotationClass;
    }

    @Override
    @NotNull
    public Class<A> getAppointAnnotationClazz() {
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
