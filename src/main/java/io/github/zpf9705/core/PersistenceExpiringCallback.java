package io.github.zpf9705.core;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.ArrayUtil;

import java.lang.reflect.Method;

/**
 * Call cache deletion method after the callback class, for persistent files deleted
 * Using {@link cn.hutool.Hutool} encapsulation of spring-based tool to realize the unity of the proxy object callback
 *
 * @author zpf
 * @since 1.1.0
 */
public class PersistenceExpiringCallback extends SimpleAspect {

    private static final long serialVersionUID = -6444540838308168357L;

    @Override
    public boolean before(Object target, Method method, Object[] args) {
        //ok pass
        return true;
    }

    @Override
    public boolean afterException(Object target, Method method, Object[] args, Throwable e) {
        if (ArrayUtil.isNotEmpty(args) && args.length >= 2) {
            Console.logger.error(
                    "key {} operation error [{}]",
                    args[0],
                    e.getMessage()
            );
            return false;
        }
        return true;
    }

    @Override
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        if (ArrayUtil.isNotEmpty(args) && args.length >= 2) {
            //del Persistence of key and value
            ExpirePersistenceUtils.removePersistence(args[0], args[1]);
            return true;
        }
        return false;
    }
}
