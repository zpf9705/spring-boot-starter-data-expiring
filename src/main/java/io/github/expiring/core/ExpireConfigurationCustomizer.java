package io.github.expiring.core;

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
     * @implNote 配置方法
     */
    void customize(ExpireTemplate<K, V> template);
}
