package io.github.zpf9705.expiring.core;

/**
 * Expiry common exception and constructs for {@link RuntimeException}
 *
 * @author zpf
 * @since 2.1.1-complete
 */
public class ExpiringException extends RuntimeException {

    private static final long serialVersionUID = 7054751876464142577L;

    /**
     * Create an expiration specified exception without parameters
     */
    public ExpiringException() {
        super();
    }

    /**
     * Create an expiration exception based on a specific information
     *
     * @param message A specific exception message
     */
    public ExpiringException(String message) {
        super(message);
    }

    /**
     * Create an expiration exception based on specific information and exceptions
     *
     * @param message A specific exception message
     * @param cause   A specific exception cause
     */
    public ExpiringException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an expiration exception based on specific exceptions
     *
     * @param cause A specific exception cause
     */
    public ExpiringException(Throwable cause) {
        super(cause);
    }
}
