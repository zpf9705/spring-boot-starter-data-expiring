package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.help.expiremap.ExpireMapByteContain;

/**
 * Core cache to help implement the interface , Establish the cache help center
 * <p>
 * Example:
 * <ul>
 *     <li>{@link io.github.zpf9705.expiring.help.expiremap.ExpireMapCenter}</li>
 *     <li>...</li>
 * </ul>
 *
 * @author zpf
 * @since 3.0.0
 */
@FunctionalInterface
public interface HelpCenter<T> {

    /**
     * Get helper for cache center
     *
     * @return cache center
     */
    T getHelpCenter();

    /**
     * Get a Singleton instance for {@link ExpireMapByteContain}
     *
     * @return Singleton
     */
    default ExpireMapByteContain getContain() {
        return Singleton.EXPIRE_MAP_BYTE_CONTAIN;
    }

    /**
     * <ul>
     *     <li>{@link ExpireMapByteContain}</li>
     * </ul>
     */
    class Singleton {
        static final ExpireMapByteContain EXPIRE_MAP_BYTE_CONTAIN = new ExpireMapByteContain(100);
    }
}
