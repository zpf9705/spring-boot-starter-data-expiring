package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.autoconfigure.ExpireMapCacheProperties;

/**
 * Interface-oriented configuration integration needs to be combined auto Load Configuration
 *           {@link org.springframework.context.annotation.Bean}
 *           {@link org.springframework.beans.factory.ObjectProvider}
 *
 * @author zpf
 * @since 1.1.0
 */
public interface ConfigurationCustomizer {

    /**
     * setting method for {@link ExpireMapCacheProperties} Configuration
     *
     * @param properties {@link ExpireMapCacheProperties}
     */
    void customize(ExpireMapCacheProperties properties);
}
