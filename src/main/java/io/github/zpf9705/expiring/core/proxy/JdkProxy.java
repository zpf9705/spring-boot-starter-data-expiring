package io.github.zpf9705.expiring.core.proxy;

import cn.hutool.aop.ProxyUtil;

import java.lang.reflect.InvocationHandler;

/**
 * The JDK proxy object generation solution method
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class JdkProxy {

    final static DefaultJdkProxyInvocationHandler DEFAULT_HANDLER = new DefaultJdkProxyInvocationHandler();

    public static <T> T create(Class<T> interfaces) {
        return ProxyUtil.newProxyInstance(DEFAULT_HANDLER, interfaces);
    }

    public static <T> T create(InvocationHandler invocationHandler, Class<T> interfaces) {
        return ProxyUtil.newProxyInstance(invocationHandler, interfaces);
    }

    public static <T> T create(ClassLoader classLoader, InvocationHandler invocationHandler, Class<T> interfaces) {
        return ProxyUtil.newProxyInstance(classLoader, invocationHandler, interfaces);
    }
}
