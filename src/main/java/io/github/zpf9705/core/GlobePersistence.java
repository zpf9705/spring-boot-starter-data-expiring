package io.github.zpf9705.core;

import java.util.concurrent.TimeUnit;


/**
 * <p>
 *    Global persistence Unified method interface
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public interface GlobePersistence<K, V> {

    boolean persistenceExist();

    void setExpirationPersistence(Long duration, TimeUnit timeUnit);

    void resetExpirationPersistence();

    void replacePersistence(V newValue);

    void removePersistence();

    boolean expireOfCache();

    Entry<K, V> getEntry();

    ExpireGlobePersistence.Persistence<K, V> getPersistence();
}
