package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.core.persistence.Configuration;
import io.github.zpf9705.expiring.core.persistence.ExpireByteGlobePersistence;
import io.github.zpf9705.expiring.core.persistence.PersistenceRenewFactory;
import io.github.zpf9705.expiring.util.SystemUtils;
import net.jodah.expiringmap.ExpirationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration properties for expiring
 *
 * @author zpf
 * @since 3.0.0
 */
@ConfigurationProperties(prefix = "spring.data.expiry")
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
    private Class<? extends PersistenceRenewFactory> persistenceFactoryClass = ExpireByteGlobePersistence.class;

    /**
     * Attention : If you offer the persistent path ,
     * Will automatically on your path to create persistent file ,
     * If not we will create in the root of your project directory
     *
     * @since 1.1.4
     */
    private String persistencePath = SystemUtils.getCurrentProjectPath() + "/expire/";

    /**
     * Monitor the path information of cache recovery and implement the class collection
     * path of {@link io.github.zpf9705.expiring.core.persistence.ListeningRecovery}
     */
    private String[] listeningRecoverySubPath = ApplicationUtils.findSpringApplicationPackageName();

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
     * Set a {@code client} for help source
     */
    private Client client = Client.EXPIRE_MAP;

    /**
     * Expiry implement for {@link net.jodah.expiringmap.ExpiringMap}
     */
    private ExpiringMap expiringMap = new ExpiringMap();

    public Boolean getOpenPersistence() {
        return openPersistence;
    }

    public void setOpenPersistence(Boolean openPersistence) {
        this.openPersistence = openPersistence;
    }

    public Boolean getPersistenceAsync() {
        return persistenceAsync;
    }

    public void setPersistenceAsync(Boolean persistenceAsync) {
        this.persistenceAsync = persistenceAsync;
    }

    public Class<? extends PersistenceRenewFactory> getPersistenceFactoryClass() {
        return persistenceFactoryClass;
    }

    public void setPersistenceFactoryClass(Class<? extends PersistenceRenewFactory> persistenceFactoryClass) {
        this.persistenceFactoryClass = persistenceFactoryClass;
    }

    public String getPersistencePath() {
        return persistencePath;
    }

    public void setPersistencePath(String persistencePath) {
        this.persistencePath = persistencePath;
    }

    public Long getNoPersistenceOfExpireTime() {
        return noPersistenceOfExpireTime;
    }

    public void setNoPersistenceOfExpireTime(Long noPersistenceOfExpireTime) {
        this.noPersistenceOfExpireTime = noPersistenceOfExpireTime;
    }

    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        return noPersistenceOfExpireTimeUnit;
    }

    public void setNoPersistenceOfExpireTimeUnit(TimeUnit noPersistenceOfExpireTimeUnit) {
        this.noPersistenceOfExpireTimeUnit = noPersistenceOfExpireTimeUnit;
    }

    public String[] getListeningRecoverySubPath() {
        return listeningRecoverySubPath;
    }

    public void setListeningRecoverySubPath(String[] listeningRecoverySubPath) {
        this.listeningRecoverySubPath = listeningRecoverySubPath;
    }

    public Long getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public void setDefaultExpireTime(Long defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    public TimeUnit getDefaultExpireTimeUnit() {
        return defaultExpireTimeUnit;
    }

    public void setDefaultExpireTimeUnit(TimeUnit defaultExpireTimeUnit) {
        this.defaultExpireTimeUnit = defaultExpireTimeUnit;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ExpiringMap getExpiringMap() {
        return expiringMap;
    }

    public void setExpiringMap(ExpiringMap expiringMap) {
        this.expiringMap = expiringMap;
    }

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
         * <p>
         * If it is null, the default is to use springboot to start the package path where the main class is located
         */
        private String[] listeningPackages = ApplicationUtils.findSpringApplicationPackageName();

        public Integer getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize) {
            this.maxSize = maxSize;
        }

        public ExpirationPolicy getExpirationPolicy() {
            return expirationPolicy;
        }

        public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
            this.expirationPolicy = expirationPolicy;
        }

        public String[] getListeningPackages() {
            return listeningPackages;
        }

        public void setListeningPackages(String[] listeningPackages) {
            this.listeningPackages = listeningPackages;
        }
    }

    /**
     * Cache Client
     */
    public enum Client {
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
        SystemUtils.setProperty(Configuration.chooseClient, this.client.name());
        SystemUtils.setProperty(Configuration.listeningRecoverySubPath, Arrays.toString(this.listeningRecoverySubPath));
    }
}
