package io.github.zpf9705.expiring.core.serializer;

/**
 * The key/value unified serializable interface
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpiringSerializer<T> {

    /**
     * serialize obj
     *
     * @param t target
     * @return byte arrays
     */
    byte[] serialize(T t);

    /**
     * deserialize obj
     *
     * @param bytes byte arrays
     * @return target
     */
    T deserialize(byte[] bytes);

    /**
     * get class type
     *
     * @return target class
     */
    Class<T> serializerType();
}
