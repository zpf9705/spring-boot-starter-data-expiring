package io.github.zpf9705.expiring.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper for Able Conceptual
 * <ul>
 *     <li>{@link Closeable}</li>
 *     <li>...</li>
 * </ul>
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbleUtils {

    /**
     * Close any resource
     *
     * @param closeables {@link Closeable}
     */
    public static void close(Closeable... closeables) {
        if (ArrayUtils.simpleIsEmpty(closeables)) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
