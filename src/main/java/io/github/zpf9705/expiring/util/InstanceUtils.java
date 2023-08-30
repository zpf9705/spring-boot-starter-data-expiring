package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.ExpiringException;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.lang.reflect.Constructor;

/**
 * The simple construction of classes based on class objects is pear blossom
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class InstanceUtils {

    /**
     * According to the class object and parameter array object instantiation
     *
     * @param clazz class type {@literal no null}
     * @param param Parameters of the array
     * @param <T>   class generic
     * @return result instance object
     */
    public static <T> T instanceWithArgs(@NotNull Class<T> clazz, Object... param) {
        return instanceWithConstruct(getConstructor(clazz, getParamClasses(param)), param);
    }

    /**
     * According to the construction method and relevant parameter value object instantiation
     *
     * @param constructor must no be {@literal null}
     * @param args        params
     * @param <T>         class generic
     * @return result instance object
     */
    public static <T> T instanceWithConstruct(@NotNull Constructor<T> constructor, Object... args) {
        T result;
        try {
            result = constructor.newInstance(args);
        } catch (Throwable e) {
            //Unified throw the biggest anomaly
            throw new ExpiringException(e.getMessage());
        }
        return result;
    }

    /**
     * According to the class object and class object of the specified argument to
     * obtain the corresponding construction method
     *
     * @param clazz      class type {@literal no null}
     * @param paramClass param type
     * @param <T>        class generic
     * @return {@link Constructor}
     */
    public static <T> Constructor<T> getConstructor(@NotNull Class<T> clazz, Class<?>... paramClass) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getConstructor(paramClass);
        } catch (Throwable e) {
            //Unified throw the biggest anomaly
            throw new ExpiringException(e.getMessage());
        }
        return constructor;
    }

    /**
     * Order to obtain the parameters array class object
     *
     * @param param Parameters of the array
     * @return The class object array
     */
    public static Class<?>[] getParamClasses(Object... param) {
        Class<?>[] classes = new Class<?>[param.length];
        if (ArrayUtils.simpleIsEmpty(param)) {
            return classes;
        }
        for (int i = 0; i < param.length; i++) {
            Object o = param[i];
            //If it is null directly the next cycle
            if (o == null) {
                continue;
            }
            classes[i] = o.getClass();
        }
        return classes;
    }
}
