package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

/**
 * Request client type enums
 *
 * @author zpf
 * @since 3.1.0
 */
public enum ClientType {

    HTTP(HttpClient.class), RPC(null);

    final Class<?> clazz;

    ClientType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
