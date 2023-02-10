package io.github.expiring.core;

import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * Configuration properties for expiring map .
 *
 * @author zpf
 * @since 1.1.0
 */
@ConfigurationProperties(prefix = "expire.cache.globe-config")
@Getter
@Setter
public class ExpireMapCacheProperties {

    /**
     * map - max - size
     *
     * @see ConcurrentHashMap#size()
     */
    private Integer maxSize = 500;

    /**
     * default expiring time
     */
    private Long defaultExpireTime = 30L;

    /**
     * default expiring time unit
     */
    private TimeUnit defaultExpireTimeUnit = TimeUnit.SECONDS;

    /**
     * default expiring time policy
     * @see ExpirationPolicy
     */
    private ExpirationPolicy expirationPolicy = ExpirationPolicy.ACCESSED;

    /**
     * expire listening packages path
     */
    private String listeningPackages = "com.bookuu";

    /**
     *  Persistence setting
     */
    private Persistence persistence = new Persistence();

    @Getter
    @Setter
    static class Persistence{

        /**
         * Whether to open the persistence
         */
        private Boolean openPersistence = false;

        /**
         * Persistent path
         */
        private String persistencePath = "/home";

        /**
         * No persistence time the most value (that is less than all of this time are not given persistent)
         */
        private Long noPersistenceOfExpireTime = 20L;

        /**
         * No persistence unit of time the most value
         */
        private TimeUnit noPersistenceOfExpireTimeUnit = TimeUnit.SECONDS;
    }
}
