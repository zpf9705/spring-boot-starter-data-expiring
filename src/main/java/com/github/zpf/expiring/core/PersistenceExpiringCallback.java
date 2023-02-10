package com.github.zpf.expiring.core;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.ArrayUtil;

import java.lang.reflect.Method;

/**
 * <p>
 * Delete persistence expired callback
 * <p>
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
                    "key {} value {} operation error [{}]",
                    args[0],
                    args[1],
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
