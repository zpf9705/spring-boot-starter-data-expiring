package io.github.zpf9705.expiring.spring_jdk.example_sdk;

/**
 * SDK related attribute method definition interface,mainly including URL concatenation .
 * <p>
 * Request scheme selection, and related custom enumeration names .
 *
 * @author zpf
 * @since 3.1.0
 */
public interface SdkEnum {

    /**
     * Get request url , generally, string formatting is required
     *
     * @param host host address
     * @return request url address
     */
    String getUlr(String host);

    /**
     * Select the corresponding request scheme based on this enumeration identifier,
     * currently supporting the type of HTTP
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

    /**
     * API Request Address HTTP Protocol Header Enumeration Selection
     */
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

    /**
     * Enumeration of currently supported types for HTTP
     */
    enum RequestMethod {
        GET, PUT, POST, DELETE
    }
}
