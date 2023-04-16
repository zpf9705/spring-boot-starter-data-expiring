package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Cache expiration project simulation Helper accessors ,
 * its performance in the form of a Helper factory
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireAccessor implements InitializingBean {

    private ExpireHelperFactory helperFactory;

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(getHelperFactory() != null, "ExpireHelperFactory must required");
    }

    /**
     * Get the expiry Helper factory
     *
     * @return The HelperFactory to get.
     */
    public ExpireHelperFactory getHelperFactory() {
        return this.helperFactory;
    }

    /**
     * Set the expiry Helper factory.
     *
     * @param helperFactory The connectionFactory to set.
     */
    public void setHelperFactory(@NonNull ExpireHelperFactory helperFactory) {
        this.helperFactory = helperFactory;
    }
}
