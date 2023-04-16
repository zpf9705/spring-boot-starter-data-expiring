package io.github.zpf9705.expiring.help.expiremap;


import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serialization in the key do not match the value ，the original value of the preserved
 * Serialize {@link Arrays#equals(Object[], Object[])} method by using the value passed
 * in comparison with the cache serializable value to find the original value
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapByteContain extends ConcurrentHashMap<byte[], byte[]> {

    private static final long serialVersionUID = -851217802015189183L;

    public ExpireMapByteContain(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Add {@code bytes} to map
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     */
    public void addBytes(byte[] key, byte[] value) {
        if (key == null || value == null) {
            return;
        }
        byte[] similarKey = getSimilarBytesForKey(key);
        byte[] similarValue = getSimilarBytesForValue(value);
        if (similarKey != null && similarValue != null) {
            this.remove(similarKey, similarValue);
        } else {
            if (similarKey != null) {
                this.remove(similarKey);
            }
        }
        this.put(key, value);
    }

    /**
     * Get similar key bytes for {@code newBytesKey}
     *
     * @param newBytesKey new bytes for {@code object}
     * @return bytes
     */
    public byte[] getSimilarBytesForKey(byte[] newBytesKey) {
        if (newBytesKey == null) {
            return null;
        }
        return this.filter(this.keySet(), newBytesKey);
    }

    /**
     * Get similar value bytes for {@code newBytesKey}
     *
     * @param newBytesValue new bytes for {@code object}
     * @return bytes
     */
    public byte[] getSimilarBytesForValue(byte[] newBytesValue) {
        if (newBytesValue == null) {
            return null;
        }
        return this.filter(this.values(), newBytesValue);
    }

    /**
     * Exit in key/value similar Bytes
     *
     * @param bytes new bytes
     * @return exist in cache bytes list
     */
    public boolean existKey(byte[] bytes) {
        return getSimilarBytesForKey(bytes) != null;
    }

    /**
     * Exit in key/value similar Bytes
     *
     * @param bytes new bytes
     * @return exist in cache bytes list
     */
    public boolean existValue(byte[] bytes) {
        return getSimilarBytesForValue(bytes) != null;
    }

    /**
     * Filter similar element in collection
     *
     * @param collection must not be {@literal null}
     * @param newBytes   must not be {@literal null}
     * @return similar element
     */
    private byte[] filter(Collection<byte[]> collection, byte[] newBytes) {
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
        return collection.stream().filter(b -> Arrays.equals(b, newBytes))
                .findFirst().orElse(null);
    }
}
