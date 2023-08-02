package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import cn.hutool.core.util.ReflectUtil;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Request the client tool class to route parameters {@link Request} for simple and efficient resolution of request.
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class ClientUtils {

    /**
     * Unified request method, currently only supporting HTTP
     *
     * @param request Request parameter encapsulation class
     * @param host    host address
     * @param <R>     {@link Response}
     * @return {@link Response}
     */
    public static <R extends Response> R execute(String host, Request<R> request) {
        return getClient(host, request).request();
    }

    /**
     * Unified request method, currently only supporting HTTP
     *
     * @param request Request parameter encapsulation class of {@link Supplier}
     * @param host    host address
     * @param <R>     {@link Response}
     * @return {@link Response}
     */
    public static <R extends Response> R execute(@NotNull Supplier<String> host, Request<R> request) {
        return execute(host.get(), request);
    }

    /**
     * Obtain and cache a single instance {@link Client} centered on non-repeating single urls, as shown in
     * {@link AbstractClient}
     *
     * @param host    host address
     * @param request Request parameter encapsulation class
     * @param <R>     Response data generalization
     * @return Client singleton object
     */
    @SuppressWarnings("unchecked")
    private static <R extends Response> Client<R> getClient(String host, Request<R> request) {
        return AbstractClient.getClient(() -> {
            //Building client objects through reflection based on client type (provided that they are not cached)
            Object instance = ReflectUtil.newInstance(request.getClientType().getClazz(),
                    //Format Request Address
                    request.formatUrl(host));
            if (instance instanceof Client) {
                return (Client<R>) instance;
            }
            throw new NoSuchElementException(instance.toString());
        }, request, host);
    }
}
