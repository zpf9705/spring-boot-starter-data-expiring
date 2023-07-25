package io.github.zpf9705.expiring.spring_jdk.example_sdk;

/**
 * Public Abstract Enumeration Class SDK Interface
 *
 * @author zpf
 * @since 3.1.0
 */
public interface SdkEnum {

    /**
     * Get request URL
     *
     * @param host host address
     */
    String getUlr(String host);

    /**
     * Obtain Request Method
     *
     * @return {@link RequestMethod}
     */
    RequestMethod getRequestMethod();

    /**
     * Get Enumeration Name
     *
     * @return {@link Enum#name()}
     */
    String name();


    enum ApiProtocol {

        HTTPS("https:"),

        HTTP("http:");

        private final String path;

        ApiProtocol(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    enum RequestMethod {
        GET, PUT, POST, DELETE
    }
}
