package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.CanNull;

import java.lang.reflect.Array;

/**
 * Simple array calibration tool
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ArrayUtils {

    /**
     * Is Empty for {@code Array}
     *
     * @param array Check array
     * @param <T>   {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <T> boolean simpleIsEmpty(@CanNull T[] array) {
        return !simpleNotEmpty(array);
    }

    /**
     * Is not Empty for {@code Array}
     *
     * @param array Check array
     * @param <T>   {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <T> boolean simpleNotEmpty(@CanNull T[] array) {
        return null != array && !(array.length == 0);
    }

    /**
     * Check Whether is arrayed
     *
     * @param t   check obj
     * @param <T> object generic
     * @return {@literal true} is array
     */
    public static <T> boolean isArray(T t) {
        return t != null && t.getClass().isArray();
    }

    /**
     * Create an empty array
     *
     * @param clazz   Element Type
     * @param newSize size
     * @param <T>     array element type
     * @return Empty array
     * @since 3.1.2
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> clazz, int newSize) {
        return (T[]) Array.newInstance(clazz, newSize);
    }
}
