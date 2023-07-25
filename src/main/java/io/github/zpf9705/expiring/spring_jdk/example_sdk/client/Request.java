package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import cn.hutool.core.bean.BeanUtil;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.SdkEnum;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.SdkException;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Define Request Nodes
 *
 * @author zpf
 * @since 3.1.0
 */
public interface Request<R extends Response> extends Serializable {

    /**
     * Affiliated API
     *
     * @return api enum
     */
    SdkEnum matchApi();

    /**
     * Obtain request uri
     *
     * @param host Request host address
     * @return request url
     */
    default String formatUrl(String host) {
        return matchApi().getUlr(host);
    }

    /**
     * Get Request Body Object
     *
     * @return Request Body Object
     */
    Object getBody();

    /**
     * Default parameter conversion to map method，be dependent on{@link #getBody()}
     *
     * @return The map form of the parameter body
     */
    default Map<String, Object> toParamsMap() {
        Object body = getBody();
        if (Objects.isNull(body)) {
            return Collections.emptyMap();
        }
        return BeanUtil.beanToMap(body, new LinkedHashMap<>(),
                false, true);
    }

    /**
     * Parameter verification
     */
    void validate() throws SdkException;

    /**
     * Return the response class object
     *
     * @return API - Response Object
     */
    Class<R> getResponseCls();

    /**
     * Get request header map
     *
     * @return {@link Map}
     */
    Map<String, String> getHeadMap();

    /**
     * Determine if the request response data type is {@code JSON}
     *
     * @return {@literal true} all right
     */
    default boolean isrResponseJsonType() {
        return true;
    }

    /**
     * Determine if the request response data type is {@code XML}
     *
     * @return {@literal true} all right
     */
    default boolean isrResponseXmlType() {
        return false;
    }

    /**
     * XML conversion function
     *
     * @return {@literal true} all right
     */
    Function<String, String> xmlConvert();

    /**
     * Special conversion requirements
     *
     * @return {@link Function}
     */
    Function<String, String> specialConvert();

    /**
     * Obtain request type (default is all HTTP)
     *
     * @return {@link ClientType}
     */
    ClientType getClientType();
}
