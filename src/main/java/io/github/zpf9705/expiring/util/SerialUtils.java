package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.annotation.CanNull;

import java.io.*;

/**
 * A static tool for serialization and deserialization
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class SerialUtils {

    static final int init_size = 1024;

    /**
     * A given object serialization to a byte array.
     *
     * @param object the object to serialize
     * @return A byte array, on behalf of the moving object
     */
    @CanNull
    public static byte[] serialize(@CanNull Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream(init_size);
        try (ObjectOutputStream oos = new ObjectOutputStream(stream)) {
            oos.writeObject(object);
            oos.flush();
        } catch (Throwable e) {
            throw new OperationsException("Failed to serialize object with ex msg" + e.getMessage());
        }
        return stream.toByteArray();
    }

    /**
     * Deserialized object into a byte array.
     *
     * @param bytes a serialized object
     * @return The results of the deserialization bytes
     */
    @CanNull
    public static Object deserialize(@CanNull byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return ois.readObject();
        } catch (Throwable e) {
            throw new OperationsException("Failed to deserialize bytes with ex msg" + e.getMessage());
        }
    }
}
