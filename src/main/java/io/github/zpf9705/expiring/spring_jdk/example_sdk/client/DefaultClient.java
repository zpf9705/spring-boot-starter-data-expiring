package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * Default Request Client
 *
 * @author zpf
 * @since 3.1.0
 */
public final class DefaultClient<T extends Response> extends AbstractClient<T> {

    private static final long serialVersionUID = -8853507311212423865L;

    /* ******* super Constructs ***********/

    DefaultClient(String url) {
        super(url);
    }

    @Override
    public T request() {
        throw new UnsupportedOperationException("default client no operation");
    }
}
