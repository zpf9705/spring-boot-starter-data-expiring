package io.github.zpf9705.expiring.autoconfigure;

/**
 * Global persistent cache restart recovery method defines the interface to spring bean automated
 *
 * @author zpf
 * @since 3.0.0
 */
public interface PersistenceCapable {

    /**
     * Log files in the recovery plan
     *
     * @param path Save the path cache files
     * @return bind result
     */
    String persistenceRegain(String path);
}
