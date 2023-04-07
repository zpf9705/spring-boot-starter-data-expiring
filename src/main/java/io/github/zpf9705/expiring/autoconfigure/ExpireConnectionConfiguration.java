package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.core.ExpireProperties;

/**
 * Base Expire connection configuration.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireConnectionConfiguration {

    public final ExpireProperties properties;

    public ExpireConnectionConfiguration(ExpireProperties properties) {
        this.properties = properties;
    }

    public final ExpireProperties getProperties() {
        return this.properties;
    }
}
