package io.github.zpf9705.expiring.autoconfigure;

/**
 * Definition of persistent automatic assembly scheme loading cache to restore some files
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
