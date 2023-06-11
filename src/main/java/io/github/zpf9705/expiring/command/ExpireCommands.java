package io.github.zpf9705.expiring.command;

/**
 * Interface for the commands supported by expire.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireCommands extends ExpireStringCommands, ExpireKeyCommands {

    /**
     * Restore the key value of byte type of care
     * <pre>{@code
     * Map<byte[],byte[]> map = new HashMap<>()
     * map.put('123','456')
     * but map.get('123') == null
     * }</pre>
     * So restart need restore this byte type
     *
     * @param key   must not be null {@code key}
     * @param value must not be null {@code value}
     */
    void restoreByteType(byte[] key, byte[] value);
}
