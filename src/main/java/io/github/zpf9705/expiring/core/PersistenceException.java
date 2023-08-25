package io.github.zpf9705.expiring.core;


/**
 * Cache persistent exception
 *
 * @author zpf
 * @since 1.1.0
 */
public class PersistenceException extends ExpiringException {

    private static final long serialVersionUID = 8520468345693528171L;

    private static final String PERSISTENCE_EXCEPTION_PREFIX = "Persistence exception : ";

    /*
     * @since 2.1.1-complete
     */
    public PersistenceException() {
        super(PERSISTENCE_EXCEPTION_PREFIX);
    }

    /*
     * @since 2.1.1-complete
     */
    public PersistenceException(String message) {
        super(PERSISTENCE_EXCEPTION_PREFIX + message);
    }

    /*
     * @since 2.1.1-complete
     */
    public PersistenceException(String message, Throwable cause) {
        super(PERSISTENCE_EXCEPTION_PREFIX + message, cause);
    }
}
