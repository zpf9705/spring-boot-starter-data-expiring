package io.github.zpf9705.expiring.help;

import org.springframework.lang.NonNull;

/**
 * Thread-safe factory of Expire helpers
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireHelperFactory {

    /**
     * Provides a suitable helper for interacting with expire.
     *
     * @return Helper for interacting with expiry.
     */
    @NonNull
    ExpireHelper getHelper();
}
