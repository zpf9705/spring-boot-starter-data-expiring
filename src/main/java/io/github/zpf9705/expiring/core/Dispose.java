package io.github.zpf9705.expiring.core;

/**
 * The cache persistence operation interface for {@link ExpirePersistenceUtils}
 *
 * @author zpf
 * @since 3.0.0
 */
public interface Dispose {

    void dispose(Object[] args);
}
