package com.github.zpf.expiring.core;

/**
 * <p>
 *     This interface can help you at a code level configuration cache configuration you need
 * </p>
 * @see ExpireMapCacheProperties
 *
 * @author zpf
 * @since 1.1.0
 */
public interface ConfigurationCustomizer {

    /**
     * @implNote 配置方法
     */
    void customize(ExpireMapCacheProperties properties);
}
