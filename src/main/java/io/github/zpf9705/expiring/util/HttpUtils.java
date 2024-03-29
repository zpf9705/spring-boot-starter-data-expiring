package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * The HTTP client request tool class mainly includes four request methods: post, get, put, and del
 *
 * @author zpf
 * @since 3.1.0
 */
public final class HttpUtils {

    private HttpUtils() {
        super();
    }

    /**
     * Get request
     *
     * @param url     Request url
     * @param headers Header information
     * @param params  Get Splice Request Parameters
     * @return The {@code String} type of the return value
     */
    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        return doRequest(new HttpGet(getURIByUrlAndParams(url, params)), headers, null);
    }

    /**
     * Post Request
     *
     * @param url     Request url
     * @param json    JSON data input parameter
     * @param headers Header information
     * @return The {@code String} type of the return value
     */
    public static String post(String url, Map<String, String> headers, String json) {
        return doRequest(new HttpPost(url), headers, json);
    }

    /**
     * Put Request
     *
     * @param url     request url
     * @param headers Header information
     * @param json    JSON data input parameter
     * @return The {@code String} type of the return value
     */
    public static String put(String url, Map<String, String> headers, String json) {
        return doRequest(new HttpPut(url), headers, json);
    }

    /**
     * Delete Request
     *
     * @param url     request url
     * @param headers Header information
     * @return The {@code String} type of the return value
     */
    public static String delete(String url, Map<String, String> headers) {
        return doRequest(new HttpDelete(url), headers, null);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     * @param headers     Header information map.
     * @param json        JSON data input parameter
     * @return The {@code String} type of the return value
     */
    public static String doRequest(@NotNull HttpRequestBase requestBase,
                                   Map<String, String> headers,
                                   String json) {
        CloseableHttpClient client = HttpClients.custom().build();
        CloseableHttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            setEntity(json, requestBase);
            response = client.execute(requestBase);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new UtilsException(e.getMessage());
        } finally {
            AbleUtils.close(response);
            AbleUtils.close(client);
        }
        return result;
    }

    /**
     * Set {@link HttpEntity}.
     *
     * @param json        JSON data input parameter
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}
     */
    private static void setEntity(String json, @NotNull HttpRequestBase requestBase) {
        if (StringUtils.simpleNotBlank(json) && requestBase instanceof HttpEntityEnclosingRequestBase) {
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) requestBase;
            base.setEntity(stringEntity);
        }
    }

    /**
     * Add this request body information body.
     *
     * @param headers     Header information map.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     */
    private static void addHeaders(Map<String, String> headers, @NotNull HttpRequestBase requestBase) {
        if (!CollectionUtils.simpleIsEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * Splice Get Request Address.
     *
     * @param url    Request URL address
     * @param params ? Rear splicing parameters
     * @return URL {@link URI}
     */
    private static URI getURIByUrlAndParams(String url, Map<String, Object> params) {
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (!CollectionUtils.simpleIsEmpty(params)) {
                for (String paramKey : params.keySet()) {
                    uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                }
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new UtilsException(url + " no a valid url");
        }
        return uri;
    }
}
