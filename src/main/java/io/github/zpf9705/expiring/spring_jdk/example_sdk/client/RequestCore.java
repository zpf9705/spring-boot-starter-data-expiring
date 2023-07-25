package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * Request Core Method interface
 *
 * @author zpf
 * @since 3.1.0
 */
@FunctionalInterface
public interface RequestCore<R extends Response> {

    /**
     * request method
     *
     * @return {@link Response}
     */
    R request();
}
