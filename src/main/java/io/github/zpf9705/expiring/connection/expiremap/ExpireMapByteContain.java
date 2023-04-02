package io.github.zpf9705.expiring.connection.expiremap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Serialization in the key do not match the value ，the original value of the preserved
 * Serialize {@link Arrays#equals(Object[], Object[])} method by using the value passed
 * in comparison with the cache serializable value to find the original value
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapByteContain extends ArrayList<byte[]> {

    private static final long serialVersionUID = -851217802015189183L;

    public ExpireMapByteContain(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Add {@code bytes} to list
     *
     * @param bytes value
     */
    public synchronized void addBytes(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        byte[] similarBytes = getSimilarBytes(bytes);
        if (similarBytes != null) {
            this.remove(bytes);
        }
        this.add(bytes);
    }

    /**
     * Get similar bytes for {@code newBytesKey}
     *
     * @param newBytesKey new bytes for {@code object}
     * @return bytes
     */
    public synchronized byte[] getSimilarBytes(byte[] newBytesKey) {
        if (newBytesKey == null) {
            return null;
        }
        return this.stream().filter(b -> Arrays.equals(b, newBytesKey))
                .findFirst().orElse(null);
    }

    /**
     * Exit in key/value similar Bytes
     *
     * @param bytes new bytes
     * @return exist in cache bytes list
     */
    public boolean exist(byte[] bytes) {
        return getSimilarBytes(bytes) != null;
    }
}
