package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.CanNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class CollectionUtils {

    static final Collection<?> EMPTY_COLL = Collections.EMPTY_LIST;

    static final Map<?, ?> EMPTY_MAP = Collections.EMPTY_MAP;

    /**
     * Is Empty for {@code Collection}
     *
     * @param collection Check Collection
     * @param <T>        {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <T> boolean simpleIsEmpty(@CanNull Collection<T> collection) {
        return !simpleNotEmpty(collection);
    }

    /**
     * Is not Empty for {@code Collection}
     *
     * @param collection Check Collection
     * @param <T>        {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <T> boolean simpleNotEmpty(@CanNull Collection<T> collection) {
        return null != collection && !EMPTY_COLL.equals(collection) && !collection.isEmpty();
    }

    /**
     * Is Empty for {@code Map}
     *
     * @param map Check Map
     * @param <K> {@code Object}
     * @param <V> {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <K, V> boolean simpleIsEmpty(@CanNull Map<K, V> map) {
        return !simpleNotEmpty(map);
    }

    /**
     * Is not Empty for {@code Map}
     *
     * @param map Check Map
     * @param <K> {@code Object}
     * @param <V> {@code Object}
     * @return if {@code true} Assertions by
     */
    public static <K, V> boolean simpleNotEmpty(@CanNull Map<K, V> map) {
        return null != map && !EMPTY_MAP.equals(map) && !map.isEmpty();
    }
}
