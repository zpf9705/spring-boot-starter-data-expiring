package io.github.zpf9705.expiring.help.expiremap;

import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation for {@link ExpireMapClientConfiguration}
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public class DefaultExpireMapClientConfiguration implements ExpireMapClientConfiguration {

    private final Integer maxSize;
    private final Long defaultExpireTime;
    private final TimeUnit defaultExpireTimeUnit;
    private final ExpirationPolicy expirationPolicy;
    private final List<ExpirationListener> syncExpirationListeners;
    private final List<ExpirationListener> asyncExpirationListeners;

    public DefaultExpireMapClientConfiguration(Integer maxSize,
                                               Long defaultExpireTime,
                                               TimeUnit defaultExpireTimeUnit,
                                               ExpirationPolicy expirationPolicy,
                                               List<ExpirationListener> syncExpirationListeners,
                                               List<ExpirationListener> asyncExpirationListeners) {
        this.maxSize = maxSize;
        this.defaultExpireTime = defaultExpireTime;
        this.defaultExpireTimeUnit = defaultExpireTimeUnit;
        this.expirationPolicy = expirationPolicy;
        this.syncExpirationListeners = syncExpirationListeners;
        this.asyncExpirationListeners = asyncExpirationListeners;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getMaxSize()
     */
    @Override
    public Integer getMaxSize() {
        return this.maxSize;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getDefaultExpireTime()
     */
    @Override
    public Long getDefaultExpireTime() {
        return this.defaultExpireTime;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getDefaultExpireTimeUnit()
     */
    @Override
    public TimeUnit getDefaultExpireTimeUnit() {
        return this.defaultExpireTimeUnit;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getExpirationPolicy()
     */
    @Override
    public ExpirationPolicy getExpirationPolicy() {
        return this.expirationPolicy;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getSyncExpirationListeners()
     */
    @Override
    public List<ExpirationListener> getSyncExpirationListeners() {
        return this.syncExpirationListeners;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.expiremap.ExpireMapClientConfiguration#getASyncExpirationListeners()
     */
    @Override
    public List<ExpirationListener> getASyncExpirationListeners() {
        return this.asyncExpirationListeners;
    }
}
