package io.github.zpf9705.core;

import java.util.concurrent.TimeUnit;


/**
 * Cache persistence methods defined interfaces
 *
 * @author zpf
 * @since 1.1.0
 */
public interface GlobePersistence<K, V> {

    /**
     * To determine whether a file exists
     *
     * @return Returns true or false
     */
    boolean persistenceExist();

    /**
     * set expiration persistent file to the hard disk
     *
     * @param duration the expiration duration
     * @param timeUnit the expiration duration timeUnit
     */
    void setExpirationPersistence(Long duration, TimeUnit timeUnit);

    /**
     * reset Expiration cache Persistence
     */
    void resetExpirationPersistence();

    /**
     * replace current value within new value
     *
     * @param newValue new value
     */
    void replacePersistence(V newValue);

    /**
     * del current Persistence
     */
    void removePersistence();

    /**
     * Determine whether under expired
     *
     * @return Returns true or false
     */
    boolean expireOfCache();

    /**
     * get current key/value group
     *
     * @return {@link Entry}
     */
    Entry<K, V> getEntry();

    /**
     * get current Persistence
     *
     * @return {@link io.github.zpf9705.core.ExpireGlobePersistence.Persistence}
     */
    ExpireGlobePersistence.Persistence<K, V> getPersistence();
}
