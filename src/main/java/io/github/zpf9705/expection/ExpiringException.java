package io.github.zpf9705.expection;

/**
 * Cache abnormal parent body
 *
 * @author zpf
 * @since 2.1.1-complete
 */
public class ExpiringException extends RuntimeException {

    public ExpiringException() {
    }

    public ExpiringException(String message) {
        super(message);
    }

    public ExpiringException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiringException(Throwable cause) {
        super(cause);
    }
}
