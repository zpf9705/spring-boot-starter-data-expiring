package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Fluent implementation of  {@link ExpireAccessor}
 * <p>
 * To achieve {@link ExpiringMap} apis , And to specify the API provides the value of persistence
 *
 * @author zpf
 * @since 1.1.0
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
