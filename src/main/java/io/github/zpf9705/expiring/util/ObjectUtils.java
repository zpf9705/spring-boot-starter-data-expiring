package io.github.zpf9705.expiring.util;

import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class ObjectUtils {

    private static final String leftParenthesis = "[";
    private static final String rightParenthesis = "]";
    private static final String leftParenthesis_ = "{";
    private static final String rightParenthesis_ = "}";
    private static final String empty = "";
    public static BiPredicate<String, String> simplerOfString =
            (compare, target) -> compare.startsWith(target)
                    || compare.endsWith(target)
                    || compare.contains(target);

    /**
     * Calculate the hash mark with object param
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return hash mark
     */
    public static <T> String rawHashWithType(@NonNull T t) {
        int hashcode = 0;
        String className = t.getClass().getName();
        if (ArrayUtil.isArray(t)) {
            if (t instanceof byte[]) {
                hashcode = Arrays.hashCode((byte[]) t);
            } else if (t instanceof long[]) {
                hashcode = Arrays.hashCode((long[]) t);
            } else if (t instanceof int[]) {
                hashcode = Arrays.hashCode((int[]) t);
            } else if (t instanceof char[]) {
                hashcode = Arrays.hashCode((char[]) t);
            } else if (t instanceof short[]) {
                hashcode = Arrays.hashCode((short[]) t);
            } else if (t instanceof float[]) {
                hashcode = Arrays.hashCode((float[]) t);
            } else if (t instanceof double[]) {
                hashcode = Arrays.hashCode((double[]) t);
            } else if (t instanceof boolean[]) {
                hashcode = Arrays.hashCode((boolean[]) t);
            } else if (t instanceof Object[]) {
                hashcode = Arrays.hashCode((Object[]) t);
            }
        }
        return hashcode + className;
    }

    /**
     * Calculate the String mark with object param
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return String mark
     */
    public static <T> String toStingWithMiddle(@NonNull T t) {
        String toString = null;
        if (t.getClass().isArray() || t instanceof Collection) {
            if (t instanceof byte[]) {
                toString = Arrays.toString((byte[]) t);
            } else if (t instanceof long[]) {
                toString = Arrays.toString((long[]) t);
            } else if (t instanceof int[]) {
                toString = Arrays.toString((int[]) t);
            } else if (t instanceof char[]) {
                toString = Arrays.toString((char[]) t);
            } else if (t instanceof short[]) {
                toString = Arrays.toString((short[]) t);
            } else if (t instanceof float[]) {
                toString = Arrays.toString((float[]) t);
            } else if (t instanceof double[]) {
                toString = Arrays.toString((double[]) t);
            } else if (t instanceof boolean[]) {
                toString = Arrays.toString((boolean[]) t);
            } else if (t instanceof Object[]) {
                toString = Arrays.toString((Object[]) t);
            } else if (t instanceof Collection) {
                toString = t.toString();
            }
            if (StringUtils.isNotBlank(toString)) {
                toString = doubleReplaceEmpty(toString, leftParenthesis, rightParenthesis);
            }
        } else {
            if (t instanceof Map) {
                toString = doubleReplaceEmpty(t.toString(), leftParenthesis_, rightParenthesis_);
            }
        }
        return toString;
    }

    /**
     * Double replace with a {@code String}
     *
     * @param target  must not be {@literal null}
     * @param source  must not be {@literal null}
     * @param source_ must not be {@literal null}
     * @return result
     */
    static String doubleReplaceEmpty(@NonNull String target, @NonNull String source,
                                     @NonNull String source_) {
        return target.replace(source, ObjectUtils.empty).replace(source_, ObjectUtils.empty);
    }

    /**
     * Find
     * <b>start</b>
     * <b>end</b>
     * <b>contain/equals</b> with {@code String}
     * be a {@link Stream}
     *
     * @param source must not be {@literal null}
     * @param target must not be {@literal null}
     * @return result
     */
    public static Stream<String> findStringSimilarElementStream(@NonNull Collection<String> source,
                                                                @NonNull String target) {
        return source.stream().filter(v -> simplerOfString.test(v, target));
    }

    /**
     * @param source must not be {@literal null}
     * @param target must not be {@literal null}
     * @return result
     * @see #findStringSimilarElementStream(Collection, String) be Collection
     */
    public static List<String> findStringSimilarElement(@NonNull Collection<String> source, @NonNull String target) {
        return findStringSimilarElementStream(source, target).collect(Collectors.toList());
    }
}
