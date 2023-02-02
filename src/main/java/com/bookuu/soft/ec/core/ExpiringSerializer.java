package com.bookuu.soft.ec.core;

/**
 * <p>
 *     The key value unified serializable interface
 *    {@link java.io.Serializable}
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
public interface ExpiringSerializer<T> {

    byte[] serialize(T t);

    T deserialize(byte[] bytes);

    Class<T> serializerType();
}
