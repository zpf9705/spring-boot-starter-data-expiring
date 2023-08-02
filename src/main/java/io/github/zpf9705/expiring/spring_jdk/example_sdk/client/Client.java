package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import java.io.Closeable;
import java.io.Serializable;

/**
 * The request client interface includes planning for request issuance, special result conversion,
 * JSON interface serialization, and can be said to be a process throughout the entire request lifecycle.
 *
 * @author zpf
 * @since 3.1.0
 */
public interface Client<R extends Response> extends RequestCore<R>, SpecialResponseConvert<R>,
        JSONDispose<R>, Closeable, Serializable {
}
