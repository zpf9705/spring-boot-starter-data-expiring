package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * The cache factory access method operation class is extracted from the encapsulation method of {@code Spring-redis}.
 * <p>
 * After the template initialization is completed, obtaining the operation object of the cache
 * factory and assigning it to the template pattern is the focus of cache operation implementation.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireAccessor implements InitializingBean {

    private ExpireHelperFactory helperFactory;

    @Override
    public void afterPropertiesSet() {
        AssertUtils.Operation.isTrue(getHelperFactory() != null, "ExpireHelperFactory must required");
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
    public void setHelperFactory(@NotNull ExpireHelperFactory helperFactory) {
        this.helperFactory = helperFactory;
    }
}
