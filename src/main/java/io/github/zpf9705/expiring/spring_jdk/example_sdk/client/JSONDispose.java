package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import org.springframework.lang.NonNull;

/**
 * Response object conversion processing after obtaining the requested JSON data
 *
 * @author zpf
 * @since 3.1.0
 */
@FunctionalInterface
public interface JSONDispose<R extends Response> {

    /**
     * Convert the processed JSON string into a request response object {@link Response}
     *
     * @param request     Request parameter encapsulation class
     * @param responseStr Request Response Data String
     * @return Converted response obj
     */
    @NonNull
    R JsonToConvertResponse(Request<R> request, String responseStr);
}
