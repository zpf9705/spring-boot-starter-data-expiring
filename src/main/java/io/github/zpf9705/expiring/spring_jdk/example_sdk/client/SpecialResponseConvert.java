package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * Request Post Transform Interface
 *
 * @author zpf
 * @since 3.1.0
 */
@FunctionalInterface
public interface SpecialResponseConvert<R extends Response> {

    /**
     * Respond to JSON data special conversion requirements
     *
     * @param request     Request parameter encapsulation class
     * @param responseStr Request Response Data String
     * @return Converted response string
     */
    String convert(Request<R> request, String responseStr);
}
