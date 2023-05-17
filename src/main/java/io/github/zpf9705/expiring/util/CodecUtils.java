package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class CodecUtils {

    /**
     * Calculate the hash mark with object param
     *
     * @param t   must not be {@literal null}.
     * @param <T> obj generic
     * @return hash mark
     */
    public static <T> String rawHashWithType(@NotNull T t) {
        int hashcode = 0;
        String className = t.getClass().getName();
        if (ArrayUtils.isArray(t)) {
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
    public static <T> String toStingBeReal(@NotNull T t) {
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
                    Object deserialize = SerialUtils.deserialize((byte[]) t);
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
     * Get the MD5 encryption with object
     *
     * @param key must not be {@literal null}
     * @param <K> key generic
     * @return byte array
     */
    public static <K> String md5Hex(@NotNull K key) {
        return DigestUtils.md5Hex(getContentBytes(key));
    }

    /**
     * Get the MD5 encryption byte array
     *
     * @param key must not be {@literal null}
     * @param <K> key generic
     * @return byte array
     */
    public static <K> byte[] getContentBytes(@NotNull K key) {
        if (key instanceof byte[]) {
            return (byte[]) key;
        }
        return key.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Byte [] to find the similar project after deserialization
     *
     * @param source must not be {@literal null}
     * @param target must not be {@literal null}
     * @return result
     */
    public static List<String> findSimilarElement(@NotNull Collection<String> source, @NotNull String target) {
        return source.stream().filter(findPredicate(target)).collect(Collectors.toList());
    }

    /**
     * Subset containing assertions
     *
     * @param target must not be {@literal null}
     * @return if {@code Predicate.test() == true} contain all
     */
    public static Predicate<String> findPredicate(@NotNull String target) {
        return s -> s.contains(target);
    }
}
