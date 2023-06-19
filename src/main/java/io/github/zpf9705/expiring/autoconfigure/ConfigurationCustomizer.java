package io.github.zpf9705.expiring.autoconfigure;

/**
 * Interface-oriented configuration integration needs to be combined auto Load Configuration
 *
 * @author zpf
 * @since 1.1.0
 */
public interface ConfigurationCustomizer {

    /**
     * Setting for {@link ExpireProperties} Configuration
     *
     * @param properties {@link ExpireProperties}
     */
    void customize(ExpireProperties properties);
}
