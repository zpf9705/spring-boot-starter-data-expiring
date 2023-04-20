package io.github.zpf9705.expiring.listener;

import net.jodah.expiringmap.ExpirationListener;

import java.io.Serializable;

/**
 * Expiry blocker with {@link ExpirationListener} and auto close expiry source elements
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpirationBytesBlocker extends ExpirationListener<byte[], byte[]>, AutoCloseable, Serializable {
}
