package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.SdkException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Request abstract node class, used to define the public parameters or methods of the real request parameter class
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R> {

    private static final long serialVersionUID = 2410395844053184527L;

    @Override
    public Map<String, String> getHeadMap() {
        return Collections.emptyMap();
    }

    @Override
    public void validate() throws SdkException {
    }

    /**
     * Request response data type, default selection is JSON
     *
     * @return {@link ResponseType}
     */
    public ResponseType responseType() {
        return ResponseType.JSON;
    }

    /**
     * Determine if the request response data type is {@code JSON}
     *
     * @return {@literal true} all right
     */
    public boolean isrResponseJsonType() {
        return Objects.equals(responseType(), ResponseType.JSON);
    }

    /**
     * Determine if the request response data type is {@code XML}
     *
     * @return {@literal true} all right
     */
    public boolean isrResponseXmlType() {
        return Objects.equals(responseType(), ResponseType.XML);
    }

    /**
     * XML conversion function {@code XML}
     *
     * @return {@literal true} all right
     */
    public Function<String, String> xmlConvert() {
        throw new UnsupportedOperationException();
    }

    /**
     * Special conversion requirements
     *
     * @return {@link Supplier}
     */
    public Function<String, String> specialConvert() {
        return null;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.HTTP;
    }

    public enum ResponseType {
        JSON, XML
    }
}
