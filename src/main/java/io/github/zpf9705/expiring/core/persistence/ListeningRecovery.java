package io.github.zpf9705.expiring.core.persistence;

import java.util.concurrent.TimeUnit;

/**
 * The listener recovery interface needs to be implemented and combined with the path
 * where {@link Configuration#listeningRecoverySubPath} is placed, and uniformly called
 * after {@link ExpireSimpleGlobePersistence#deserializeWithString(StringBuilder)} recovery.
 *
 * @author zpf
 * @since 3.2.2
 */
public interface ListeningRecovery {

    /**
     * Restore cached keys and values.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     */
    void recovery(Object key, Object value);

    /**
     * Remaining time and initial units.
     *
     * @param time     must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     */
    default void expired(Long time, TimeUnit timeUnit) {

    }
}
