package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import java.io.Closeable;
import java.io.Serializable;

/**
 * Request client interface , method for defining the entire SDK request process
 *
 * @author zpf
 * @since 3.1.0
 */
public interface Client<R extends Response> extends RequestCore<R>, SpecialResponseConvert<R>,
        JSONDispose<R>, Closeable, Serializable {
}
