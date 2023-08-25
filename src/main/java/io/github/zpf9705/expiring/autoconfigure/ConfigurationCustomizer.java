package io.github.zpf9705.expiring.autoconfigure;

/**
 * Multiple configurations of the facial interface are added to the interface for {@link ExpireProperties}.
 * <p>
 * Multiple items related to this interface can be configured in combination with the
 * class marked in {@link org.springframework.context.annotation.Bean}, obtained through
 * the {@link org.springframework.context.annotation.Configuration} method function,
 * and finally regressed to the main configuration {@link ExpireProperties}.
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
