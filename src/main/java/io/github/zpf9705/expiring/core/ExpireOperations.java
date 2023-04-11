package io.github.zpf9705.expiring.core;


import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Template pattern specification interface operation
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpireOperations<K, V> {

    @Nullable
    <T> T execute(ExpireValueCallback<T> action, boolean composeException);

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     */
    @Nullable
    Boolean delete(K key);

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null}
     */
    @Nullable
    Long delete(Collection<K> keys);

    /**
     * Delete given {@code keys} with this type [and end contains]
     *
     * @param key must not be {@literal null}.
     * @return Be removed in the similar key of key/value pair
     */
    Map<K, V> deleteType(K key);

    /**
     * Remove all currently exist in the data in memory
     *
     * @return Determine the results , If the is true the removal of success
     */
    Boolean deleteAll();

    /**
     * Judge ths key {@code key} Whether exit
     *
     * @param key must not be {@literal null}.
     * @return Determine the results , If it is true is the existence and vice does not exist
     */
    Boolean exist(K key);

    /**
     * To obtain the key serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<K> getKeySerializer();

    /**
     * To obtain the value serialized way
     *
     * @return {@link ExpiringSerializer}
     */
    ExpiringSerializer<V> getValueSerializer();

    /**
     * get a ValueOperations
     *
     * @return {@link ValueOperations}
     */
    ValueOperations<K, V> opsForValue();

    /**
     * get a ExpirationOperations
     *
     * @return {@link ValueOperations}
     */
    ExpirationOperations<K, V> opsExpirationOperations();
}
