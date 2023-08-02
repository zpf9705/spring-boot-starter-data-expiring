package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * Special conversion requirements for response strings, with the ultimate goal of JSON serialization,
 * so if the response value is traditional XML or other, we hope to convert it into JSON data through this interface
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
