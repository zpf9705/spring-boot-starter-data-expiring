package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Cache expiration project simulation connection accessors ，its performance in the form of a connection factory
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireAccessor implements InitializingBean {

    private ExpireConnectionFactory connectionFactory;

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(getConnectionFactory() != null, "ExpireConnectionFactory must required !");
    }

    /**
     * Get the expire connection factory
     *
     * @return The connectionFactory to get.
     */
    public ExpireConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    /**
     * Set the expire connection factory.
     *
     * @param connectionFactory The connectionFactory to set.
     */
    public void setConnectionFactory(@NonNull ExpireConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
