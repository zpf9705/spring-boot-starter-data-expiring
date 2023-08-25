package io.github.zpf9705.expiring.core;


import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.serializer.ExpiringSerializer;

import java.util.Collection;
import java.util.Map;

/**
 * The direct method entry of the {@link ExpireTemplate} operation template, all additions, deletions,
 * modifications, and queries related to caching, and the definition of methods should be aggregated
 * to this interface and implemented one by one in {@link ExpireTemplate}. Moreover, from the class
 * operation interface, such as {@link ValueOperations}, it needs to be accessible through this interface.
 * <p>
 * This can be seen as a summary of the overall API, which is the standardization of the template class.
 * <p>
 * In the future, the interface can be inherited by the addition of cache components and method extensions,
 * Alternatively, add and implement on top of this interface.
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpireOperations<K, V> {

    /**
     * Expire Execute unified call solutions with {@link ExpireValueCallback}
     * Used here connection factory used for uniform distribution
     *
     * @param action           expiry do action must not be {@literal null}.
     * @param composeException {@literal true} Whether to merge exception
     * @param <T>              return paradigm
     * @return return value be changed
     */
    @CanNull
    <T> T execute(ExpireValueCallback<T> action, boolean composeException);

    /**
     * Delete given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return {@literal true} if the key was removed.
     */
    @CanNull
    Boolean delete(K key);

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed. {@literal null}
     */
    @CanNull
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
     * The implementation of {@link ValueOperations} is all based on {@link ExpireTemplate},
     * so a {@link ValueOperations} can be obtained through this standard interface.
     *
     * @return {@link ValueOperations}
     */
    ValueOperations<K, V> opsForValue();

    /**
     * The implementation of {@link ExpirationOperations} is all based on {@link ExpireTemplate},
     * so a {@link ExpirationOperations} can be obtained through this standard interface.
     *
     * @return {@link ValueOperations}
     */
    ExpirationOperations<K, V> opsExpirationOperations();
}
