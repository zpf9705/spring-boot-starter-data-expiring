package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.core.annotation.NotNull;

/**
 * Overdue message keys for interface
 *
 * @author zpf
 * @since 3.0.1
 */
public interface MessageExpiryCapable {

    /**
     * Get byte [] type of {@code key}
     *
     * @return No {@literal null}
     */
    @NotNull
    byte[] getByteKey();

    /**
     * Get byte [] type of {@code value}
     *
     * @return No {@literal null}
     */
    @NotNull
    byte[] getByteValue();

    /**
     * Get object type of {@code key}
     *
     * @return No {@literal null}
     */
    @NotNull
    Object getKey();

    /**
     * Get object type of {@code value}
     *
     * @return No {@literal null}
     */
    @NotNull
    Object getValue();
}
