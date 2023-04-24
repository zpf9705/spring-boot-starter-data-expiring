package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.core.persistence.Configuration;
import io.github.zpf9705.expiring.core.persistence.PersistenceRenewFactory;
import io.github.zpf9705.expiring.util.SystemUtils;
import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Configuration properties for expiring
 *
 * @author zpf
 * @since 3.0.0
 */
@ConfigurationProperties(prefix = "spring.data.expiry")
@Getter
@Setter
public class ExpireProperties {

    /**
     * Whether to open the persistence
     */
    private Boolean openPersistence = false;

    /**
     * Whether to run persistence async
     */
    private Boolean persistenceAsync = false;

    /**
     * Persistence Renew factory class
     */
    private Class<? extends PersistenceRenewFactory> persistenceFactoryClass;

    /**
     * Attention : If you offer the persistent path ,
     * Will automatically on your path to create persistent file ,
     * If not we will create in the root of your project directory
     *
     * @since 1.1.4
     */
    private String persistencePath = SystemUtils.getCurrentProjectPath() + "/expire/";

    /**
     * No persistence time the most value (that is less than all of this time are not given persistent)
     */
    private Long noPersistenceOfExpireTime = 20L;

    /**
     * No persistence unit of time the most value
     */
    private TimeUnit noPersistenceOfExpireTimeUnit = TimeUnit.SECONDS;

    /**
     * Set a {@code defaultExpireTime} for default cache time
     */
    private Long defaultExpireTime = 30L;

    /**
     * Set a {@code defaultExpireTimeUnit} for default cache time unit
     */
    private TimeUnit defaultExpireTimeUnit = TimeUnit.SECONDS;

    /**
     * Set a {@code operationType} for help source
     */
    private OperationType operationType;

    /**
     * Expiry implement for {@link net.jodah.expiringmap.ExpiringMap}
     */
    private ExpiringMap expiringMap = new ExpiringMap();

    @Getter
    @Setter
    public static class ExpiringMap {

        /**
         * Set a {@code maxsize} for map
         */
        private Integer maxSize = 500;

        /**
         * Set a {@code expirationPolicy} for map
         */
        private ExpirationPolicy expirationPolicy = ExpirationPolicy.ACCESSED;

        /**
         * Set a {@code listening packages} for map
         */
        private String[] listeningPackages = {"com", "io", "cn", "org", "top"};
    }

    /**
     * Cache operation type
     */
    public enum OperationType {
        EXPIRE_MAP
    }

    @PostConstruct
    public void init() {
        SystemUtils.setProperty(Configuration.open_persistence, this.openPersistence);
        SystemUtils.setProperty(Configuration.persistenceRunAsync, this.persistenceAsync);
        SystemUtils.setProperty(Configuration.persistence_path, this.persistencePath);
        SystemUtils.setProperty(Configuration.defaultExpireTime, this.defaultExpireTime);
        SystemUtils.setProperty(Configuration.defaultExpireTimeUnit, this.defaultExpireTimeUnit);
        SystemUtils.setProperty(Configuration.noPersistenceOfExpireTime, this.noPersistenceOfExpireTime);
        SystemUtils.setProperty(Configuration.noPersistenceOfExpireTimeUnit, this.noPersistenceOfExpireTimeUnit);
    }
}
