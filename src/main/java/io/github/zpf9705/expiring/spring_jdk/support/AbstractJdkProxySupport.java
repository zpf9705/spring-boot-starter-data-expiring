package io.github.zpf9705.expiring.spring_jdk.support;

import cn.hutool.aop.ProxyUtil;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Abstract jdk dynamic proxy method/Spring dynamic registration support class.
 * <p>
 * The process is to first rely on the dynamic proxy {@link InvocationHandler} of JDK to create a proxy object,
 * hand it over to Spring, and when Spring calls the proxy object, execute the {@link #invoke(Object, Method, Object[])}
 * method,which performs unified logical processing and distribution within this method,
 * <p>
 * The method entrusted to Spring to create bean objects is to implement the {@link FactoryBean} interface and
 * dynamically register beans through {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * <p>
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractJdkProxySupport<T> implements InvocationHandler, FactoryBean<T> {

    private Class<T> clazz;

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }

    @Override
    public T getObject() {
        return ProxyUtil.newProxyInstance(this, this.clazz);
    }

    @Override
    public Class<?> getObjectType() {
        return this.clazz;
    }

    @Override
    public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
