package io.github.zpf9705.expiring.core;

/**
 * Cache operations abnormal
 *
 * @author zpf
 * @since 2.1.1-complete
 */
public class OperationsException extends ExpiringException {

    private static final String OPERATIONS_EXCEPTION_PREFIX = "Operations exception : ";

    public OperationsException() {
        super(OPERATIONS_EXCEPTION_PREFIX);
    }

    public OperationsException(String message) {
        super(OPERATIONS_EXCEPTION_PREFIX + message);
    }

    public OperationsException(Throwable cause) {
        super(cause);
    }
}
