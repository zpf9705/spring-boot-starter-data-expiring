package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.ExpiringException;

/**
 * Utils API to throw exception classes
 *
 * @author zpf
 * @since 3.1.6
 */
public class UtilsException extends ExpiringException {
    private static final long serialVersionUID = -4559251041221426830L;

    private static final String UTILS_EXCEPTION_PREFIX = "Utils exception : ";

    public UtilsException(String message) {
        super(UTILS_EXCEPTION_PREFIX + message);
    }
}
