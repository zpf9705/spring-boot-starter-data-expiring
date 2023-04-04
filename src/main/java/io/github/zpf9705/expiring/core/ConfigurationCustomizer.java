package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.autoconfigure.ExpireProperties;

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
     * setting method for {@link ExpireProperties} Configuration
     *
     * @param properties {@link ExpireProperties}
     */
    void customize(ExpireProperties properties);
}
