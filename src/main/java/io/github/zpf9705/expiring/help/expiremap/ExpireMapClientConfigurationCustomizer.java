package io.github.zpf9705.expiring.help.expiremap;


/**
 * Callback interface that can be implemented by beans wishing to customize the
 * {@link ExpireMapClientConfiguration} via a {@link ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder
 * LettuceClientConfiguration.LettuceClientConfigurationBuilder} whilst retaining default
 * auto-configuration.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireMapClientConfigurationCustomizer {

    /**
     * Customize the {@link ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder}.
     *
     * @param clientConfigurationBuilder the builder to customize
     */
    void customize(ExpireMapClientConfiguration.ExpireMapClientConfigurationBuilder clientConfigurationBuilder);
}
