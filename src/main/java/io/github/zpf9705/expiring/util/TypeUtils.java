package io.github.zpf9705.expiring.util;

import io.reactivex.rxjava3.core.Single;

/**
 * Tool class for type operation using {@code  io.reactivex.rxjava3}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class TypeUtils {

    /**
     * Of the object by using the {@link Single#ofType(Class)} type conversion
     *
     * @param source      To cast objects , must no be {@literal null}
     * @param targetClazz Conversion type , must no be {@literal null}
     * @param <T>         To cast objects generic
     * @param <O>         Conversion type generic
     * @return Conversion object
     */
    public static <T, O> O convert(T source, Class<O> targetClazz) {
        if (source == null || targetClazz == null) {
            return null;
        }
        return convert0(source, targetClazz);
    }

    private static <T, O> O convert0(T source, Class<O> targetClazz) {
        O convert;
        try {
            convert = Single.just(source)
                    .ofType(targetClazz)
                    .blockingGet();
        } catch (Exception e) {
            convert = null;
        }
        return convert;
    }
}
