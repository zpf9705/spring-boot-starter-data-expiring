package io.github.zpf9705.expiring.core.proxy;

import cn.hutool.aop.ProxyUtil;
import java.lang.annotation.Annotation;

/**
 * The JDK proxy object generation solution method {@link JdkBeanDefinition}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class JdkProxy {

    public static <T extends JdkBeanDefinition,
            A extends Annotation> T createProxy(JdkProxyInvocationHandler<T, A> handler) {
        return ProxyUtil.newProxyInstance(handler, handler.getTarget().getClass().getInterfaces());
    }

    public static <T extends JdkBeanDefinition,
            A extends Annotation> T createProxy(T target, Class<A> annotationClass) {
        return ProxyUtil.newProxyInstance(
                new JdkProxyInvocationTrace<>(target, annotationClass),
                target.getClass().getInterfaces());
    }
}
