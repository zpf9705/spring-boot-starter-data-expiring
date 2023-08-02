package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * The request method function interface, {@link #request()}, is the entry point for the request.
 * <p>
 * This method does not have design parameters. In order to facilitate the construction of static schemes,
 * the parameters were initially encapsulated in the thread class to better manage the current parameters.
 * <p>
 * The current parameters can be obtained at any time in various parts of the cycle, which facilitates
 * subsequent parameter extension operations
 *
 * @author zpf
 * @since 3.1.0
 */
@FunctionalInterface
public interface RequestCore<R extends Response> {

    /**
     * Request Entry Function Method
     *
     * @return Extends for {@link Response}
     */
    R request();
}
