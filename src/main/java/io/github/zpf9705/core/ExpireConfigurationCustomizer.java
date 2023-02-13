package io.github.zpf9705.core;

import net.jodah.expiringmap.ExpirationListener;

/**
 *
 * The implementation of this interface can help you with dynamic new overdue listener configuration code level
 *
 * @see ExpireTemplate#addExpiredListener(ExpirationListener)
 *
 * @author zpf
 * @since 1.1.0
 */
public interface ExpireConfigurationCustomizer<K, V> {

    /**
     * setting method for {@link ExpireTemplate} Configuration
     * @param template {@link ExpireTemplate}
     */
    void customize(ExpireTemplate<K, V> template);
}
