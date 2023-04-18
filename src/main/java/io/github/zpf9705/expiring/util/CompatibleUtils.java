package io.github.zpf9705.expiring.util;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.lang.NonNull;
import org.springframework.util.SerializationUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class CompatibleUtils {

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
            }
        }
        return hashcode + className;
    }

    /**
     * Calculate the String mark with real object param
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return String mark
     */
    public static <T> String toStingBeReal(@NonNull T t) {
        if (t instanceof String) {
            return t.toString();
        }
        String toString = null;
        if (t.getClass().isArray()) {
            if (t instanceof byte[]) {
                /*
                 * @see io.github.zpf9705.expiring.core.serializer.ExpiringSerializerAdapter
                 */
                try {
                    Object deserialize = SerializationUtils.deserialize((byte[]) t);
                    if (deserialize != null) {
                        toString = deserialize.toString();
                    }
                } catch (Exception ignore) {
                    //noting to do
                }
            }
        }
        return toString;
    }

    /**
     * Byte [] to find the similar project after deserialization
     *
     * @param source must not be {@literal null}
     * @param target must not be {@literal null}
     * @return result
     */
    public static List<String> findSimilarElement(@NonNull Collection<String> source, @NonNull String target) {
        return source.stream().filter(findPredicate(target)).collect(Collectors.toList());
    }

    /**
     * Subset containing assertions
     *
     * @param target must not be {@literal null}
     * @return if {@code Predicate.test() == true} contain all
     */
    public static Predicate<String> findPredicate(@NonNull String target) {
        return s -> s.contains(target);
    }
}
