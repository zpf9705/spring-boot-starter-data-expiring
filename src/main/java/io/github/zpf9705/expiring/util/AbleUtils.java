package io.github.zpf9705.expiring.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper for Able Conceptual
 * <ul>
 *     <li>{@link AutoCloseable}</li>
 *     <li>{@link Closeable}</li>
 * </ul>
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbleUtils {

    /**
     * Close any resource with {@link Closeable}
     *
     * @param closeables {@link Closeable}
     */
    public static void close(Closeable... closeables) {
        if (ArrayUtils.simpleIsEmpty(closeables)) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Close any resource with {@link AutoCloseable}
     *
     * @param closeables {@link AutoCloseable}
     */
    public static void close(AutoCloseable... closeables) {
        if (ArrayUtils.simpleIsEmpty(closeables)) {
            return;
        }
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
