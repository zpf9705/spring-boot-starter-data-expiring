package io.github.zpf9705.core;

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
     * setting method for {@link ExpireMapCacheProperties} Configuration
     * @param properties {@link ExpireMapCacheProperties}
     */
    void customize(ExpireMapCacheProperties properties);
}
