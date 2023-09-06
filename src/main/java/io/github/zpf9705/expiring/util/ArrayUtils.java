package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.CanNull;

/**
 * Simple array calibration tool
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ArrayUtils {

    public static final String[] emptyArray = new String[0];

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
     * Convert the string converted through the array {@link #toString()} method to the original array,
     * but not the same array object.
     *
     * @param toString Array string after toString
     * @return Convert Array
     */
    public static String[] toStringArrayToConvertArray(String toString) {
        if (StringUtils.simpleIsBlank(toString)) {
            return emptyArray;
        }
        if (!(toString.startsWith("[") && toString.endsWith("]"))) {
            throw new UtilsException("Non compliant array: the conversion string does not contain " +
                    "'[' at the beginning or ']' at the end");
        }
        toString = toString.replace("[", "").replace("]", "");
        String[] split = toString.split(",");
        if (split.length == 0) {
            throw new UtilsException("Non compliant array: does not contain commas");
        }
        return split;
    }
}
