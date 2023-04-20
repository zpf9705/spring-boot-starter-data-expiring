package io.github.zpf9705.expiring.core.proxy;

import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.lang.annotation.Annotation;

/**
 * Get a annotation for proxy subsequent
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ProxyAnnotationAware<A extends Annotation> {

    /**
     * Get a proxy class annotation
     *
     * @return a class annotation
     */
    @NotNull
    Class<A> getProxyAnnotation();
}
