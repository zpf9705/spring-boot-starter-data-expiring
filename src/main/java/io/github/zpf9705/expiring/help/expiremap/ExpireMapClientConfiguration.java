package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.util.AssertUtils;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Here is about {@link net.jodah.expiringmap.ExpiringMap} client configuration interface.
 * <p>
 * Provides the function of the configuration and obtain.
 * <p>
 * Providing optional elements allows a more specific configuration of the client.
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public interface ExpireMapClientConfiguration {

    /**
     * Obtain the maximum number of additions for {@link net.jodah.expiringmap.ExpiringMap}
     *
     * @return {@literal Integer} map max save size
     */
    Integer getMaxSize();

    /**
     * Obtain the default cache time for {@link net.jodah.expiringmap.ExpiringMap}
     *
     * @return {@literal Long} default expire time
     */
    Long getDefaultExpireTime();

    /**
     * Obtain the default cache time unit of {@link #getDefaultExpireTime()}
     *
     * @return {@literal TimeUnit} default expire time unit
     */
    TimeUnit getDefaultExpireTimeUnit();

    /**
     * For specific explanations, please refer to {@link ExpirationPolicy}
     *
     * @return {@literal ExpirationPolicy} default ExpirationPolicy
     */
    ExpirationPolicy getExpirationPolicy();

    /**
     * Get a collection of synchronization listeners
     *
     * @return {@literal ExpirationPolicy} Cluster expired to monitor interface of sync
     */
    List<ExpirationListener> getSyncExpirationListeners();

    /**
     * Get asynchronous listener collection
     *
     * @return {@literal ExpirationPolicy} Cluster expired to monitor interface of async
     */
    List<ExpirationListener> getASyncExpirationListeners();

    /**
     * Create a new {@link ExpireMapClientConfigurationBuilder} to build {@link ExpireMapClientConfiguration} to be used
     *
     * @return a new {@link ExpireMapClientConfigurationBuilder} to build {@link ExpireMapClientConfiguration}
     */
    static ExpireMapClientConfigurationBuilder builder() {
        return new ExpireMapClientConfigurationBuilder();
    }

    /**
     * Create a default new {@link ExpireMapClientConfigurationBuilder} to build {@link ExpireMapClientConfiguration}
     * <dl>
     *     <dt>max_size</dt>
     *     <dd>20*50</dd>
     *     <dt>expire_time</dt>
     *     <dd>30L</dd>
     *     <dt>expire_time_unit</dt>
     *     <dd>TimeUnit.SECONDS</dd>
     *     <dt>ExpirationPolicy</dt>
     *     <dd>ACCESSED</dd>
     * </dl>
     *
     * @return a {@link ExpireMapClientConfiguration} with defaults.
     */
    static ExpireMapClientConfiguration defaultConfiguration() {
        return builder().build();
    }

    /**
     * Simple Construction Class of {@link ExpireMapClientConfiguration}
     */
    class ExpireMapClientConfigurationBuilder {

        @CanNull
        Integer maxSize;
        @CanNull
        Long defaultExpireTime;
        @CanNull
        TimeUnit defaultExpireTimeUnit;
        @CanNull
        ExpirationPolicy expirationPolicy;
        static final Integer DEFAULT_MAX_SIZE = 20 * 50;
        static final Long DEFAULT_EXPIRE_TIME = 30L;
        static final TimeUnit DEFAULT_EXPIRE_TIME_UNIT = TimeUnit.SECONDS;
        static final ExpirationPolicy DEFAULT_EXPIRATION_POLICY = ExpirationPolicy.ACCESSED;
        final List<ExpirationListener> syncExpirationListeners = new ArrayList<>();
        final List<ExpirationListener> asyncExpirationListeners = new ArrayList<>();

        ExpireMapClientConfigurationBuilder() {
        }

        /**
         * Given the map one of the biggest capacity
         *
         * @param maxSize The maximum capacity
         * @return {@link ExpireMapClientConfigurationBuilder}
         */
        public ExpireMapClientConfigurationBuilder acquireMaxSize(Integer maxSize) {
            AssertUtils.Operation.isTrue(this.maxSize == null,
                    "MaxSize existing configuration values, please do not cover");
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Given the map of a default cache expiration time
         *
         * @param defaultExpireTime The default cache expiration time
         * @return {@link ExpireMapClientConfigurationBuilder}
         */
        public ExpireMapClientConfigurationBuilder acquireDefaultExpireTime(Long defaultExpireTime) {
            AssertUtils.Operation.isTrue(this.defaultExpireTime == null,
                    "DefaultExpireTime existing configuration values, please do not cover");
            this.defaultExpireTime = defaultExpireTime;
            return this;
        }

        /**
         * Given the map of a default cache expiration time units
         *
         * @param defaultExpireTimeUnit The default cache expiration time units
         * @return {@link ExpireMapClientConfigurationBuilder}
         */
        public ExpireMapClientConfigurationBuilder acquireDefaultExpireTimeUnit(TimeUnit defaultExpireTimeUnit) {
            AssertUtils.Operation.isTrue(this.defaultExpireTimeUnit == null,
                    "DefaultExpireTimeUnit existing configuration values, please do not cover");
            this.defaultExpireTimeUnit = defaultExpireTimeUnit;
            return this;
        }

        /**
         * Given the map of a default cache expiration expired strategy
         *
         * @param expirationPolicy The default cache expiration expired strategy
         * @return {@link ExpireMapClientConfigurationBuilder}
         */
        public ExpireMapClientConfigurationBuilder acquireDefaultExpirationPolicy(ExpirationPolicy expirationPolicy) {
            AssertUtils.Operation.isTrue(this.expirationPolicy == null,
                    "ExpirationPolicy existing configuration values, please do not cover");
            this.expirationPolicy = expirationPolicy;
            return this;
        }

        /**
         * Increase the sync expired listeners
         *
         * @param expirationListener {@link ExpirationListener}
         */
        public void addSyncExpiredListener(ExpirationListener expirationListener) {
            if (expirationListener != null) {
                this.syncExpirationListeners.add(expirationListener);
            }
        }

        /**
         * Increase the async expired listeners
         *
         * @param expirationListener {@link ExpirationListener}
         */
        public void addASyncExpiredListener(ExpirationListener expirationListener) {
            if (expirationListener != null) {
                this.asyncExpirationListeners.add(expirationListener);
            }
        }

        /**
         * Build the {@link ExpireMapClientConfiguration} with the configuration applied from this builder.
         *
         * @return a new {@link ExpireMapClientConfiguration} implementation.
         */
        public ExpireMapClientConfiguration build() {
            if (this.maxSize == null || this.maxSize == 0) {
                this.maxSize = DEFAULT_MAX_SIZE;
            }
            if (this.defaultExpireTime == null || this.defaultExpireTime == 0L) {
                this.defaultExpireTime = DEFAULT_EXPIRE_TIME;
            }
            if (this.defaultExpireTimeUnit == null) {
                this.defaultExpireTimeUnit = DEFAULT_EXPIRE_TIME_UNIT;
            }
            if (this.expirationPolicy == null) {
                this.expirationPolicy = DEFAULT_EXPIRATION_POLICY;
            }
            return new DefaultExpireMapClientConfiguration(
                    this.maxSize,
                    this.defaultExpireTime,
                    this.defaultExpireTimeUnit,
                    this.expirationPolicy,
                    this.syncExpirationListeners,
                    this.asyncExpirationListeners);
        }
    }
}
