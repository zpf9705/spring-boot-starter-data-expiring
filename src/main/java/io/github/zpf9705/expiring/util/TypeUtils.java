package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.reactivex.rxjava3.core.Single;

/**
 * Type conversion tool
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class TypeUtils {

    /**
     * Of the object by using the {@link Single#ofType(Class)} type conversion
     *
     * @param source To cast objects , must no be {@literal null}
     * @param target Conversion type , must no be {@literal null}
     * @param <T>    To cast objects generic
     * @param <O>    Conversion type generic
     * @return Conversion object
     */
    public static <T, O> O convert(@NotNull T source, @NotNull Class<O> target) {
        O o;
        try {
            o = Single.just(source).ofType(target).blockingGet();
        } catch (Exception e) {
            o = null;
        }
        return o;
    }
}
