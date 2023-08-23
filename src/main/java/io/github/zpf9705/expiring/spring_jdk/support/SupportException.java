package io.github.zpf9705.expiring.spring_jdk.support;

import io.github.zpf9705.expiring.core.ExpiringException;


/**
 * Support API to throw exception classes
 *
 * @author zpf
 * @since 3.1.6
 */
public class SupportException extends ExpiringException {

    private static final String SUPPORT_EXCEPTION_PREFIX = "Support exception : ";

    private static final long serialVersionUID = -8539516821365287546L;

    public SupportException(String message) {
        super(SUPPORT_EXCEPTION_PREFIX + message);
    }
}
