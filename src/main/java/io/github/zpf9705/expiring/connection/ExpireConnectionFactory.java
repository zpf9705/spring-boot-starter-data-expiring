package io.github.zpf9705.expiring.connection;

import org.springframework.lang.NonNull;

/**
 * Thread-safe factory of Expire connections.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireConnectionFactory {

    /**
     * Provides a suitable connection for interacting with expire.
     *
     * @return connection for interacting with Redis.
     */
    @NonNull
    ExpireConnection getConnection();

    @NonNull
    String getTemplateBeanFactoryName();
}
