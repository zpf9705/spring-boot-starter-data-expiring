package io.github.zpf9705.expiring.core.persistence;

import java.util.concurrent.TimeUnit;


/**
 * Persistent cache file is written to, change operation methods, such as defining interfaces
 *
 * @author zpf
 * @since 1.1.0
 */
public interface GlobePersistenceWriteProcess<K, V> {

    /**
     * Serial cache persistence
     */
    void serial();

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
     * Delete all the cache Persistence
     */
    void removeAllPersistence();

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
     * @return {@link ExpireSimpleGlobePersistence.Persistence}
     */
    ExpireSimpleGlobePersistence.Persistence<K, V> getPersistence();
}
