package io.github.zpf9705.expiring.listener;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.core.ExpirePersistenceUtils;

import java.lang.reflect.Method;

/**
 * Expiration of {@code key} and {@code value} for remove
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
            Console.error(
                    "Key {} operation error [{}]",
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
