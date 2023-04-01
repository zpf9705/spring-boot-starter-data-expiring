package io.github.zpf9705.expiring.core.proxy;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.proxy.JdkProxyFactory;
import io.github.zpf9705.expiring.connection.ExpireConnection;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnection;

import java.lang.annotation.Annotation;
import java.util.Arrays;

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

    public static void main(String[] args) {
        Class<?>[] interfaces = ExpireMapConnection.class.getInterfaces();
        System.out.println(Arrays.toString(interfaces));
        System.out.println(Arrays.toString(ExpireMapConnection.class.getInterfaces()));
    }
}
