package io.github.zpf9705.core;

/**
 * Interface-oriented configuration integration needs to be combined auto Load Configuration
 *
 * <ul>
 *     <p>
 *           {@link org.springframework.context.annotation.Bean}
 *           {@link org.springframework.beans.factory.ObjectProvider}
 *     </p>
 * </ul>
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
