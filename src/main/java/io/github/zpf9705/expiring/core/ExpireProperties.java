package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.persistence.PersistenceFactory;
import io.github.zpf9705.expiring.util.SystemUtils;
import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.concurrent.TimeUnit;

/**
 * Configuration properties for expiring
 *
 * @author zpf
 * @since 3.0.0
 */
@ConfigurationProperties(prefix = "expire.config")
@Getter
@Setter
public class ExpireProperties {

    /**
     * Whether to open the persistence
     */
    private Boolean openPersistence = false;

    /**
     * persistence factory class
     */
    private Class<? extends PersistenceFactory> persistenceFactoryClass;

    /**
     * Persistent path
     * attention : If you offer the persistent path ,
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
     * Set a {@code defaultExpireTime} for map
     */
    private Long defaultExpireTime = 30L;

    /**
     * Set a {@code defaultExpireTimeUnit} for map
     */
    private TimeUnit defaultExpireTimeUnit = TimeUnit.SECONDS;


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
        private String listeningPackages = "com";
    }
}
