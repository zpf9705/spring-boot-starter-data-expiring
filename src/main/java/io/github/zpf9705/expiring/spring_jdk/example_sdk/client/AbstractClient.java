package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONValidator;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.StringUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Abstract client class, defining some common methods in this class
 *
 * @author zpf
 * @since 3.1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient<R extends Response> implements Client<R> {

    private static final long serialVersionUID = 8084820515803632476L;

    private static final Object lock = new Object();

    //Cache request clients for each request object to prevent memory waste caused by multiple new requests
    private static final Map<String, Client> CLIENT_CACHE = new ConcurrentHashMap<>(16);

    //Save each request parameter and use it for subsequent requests
    private static final NamedThreadLocal<Request> PARAM_NAMED_SAVER = new NamedThreadLocal<>("CURRENT API CLIENT SAVER");

    // empty json object
    static final String empty_json = "{}";

    //Requested remote interface address
    private final String url;

    /* ******* Constructs ***********/

    public AbstractClient(String url) {
        AssertUtils.Operation.hasText(url, "RequestUrl can not be null !");
        this.url = url;
    }

    /**
     * Get Cache Client
     *
     * @param url Link Address
     * @return {@link AbstractClient}
     */
    static <R extends Response> Client<R> getCacheClient(String url) {
        if (StringUtils.simpleIsBlank(url)) {
            return null;
        }
        return CLIENT_CACHE.get(url);
    }

    /**
     * Caching client objects
     *
     * @param url    Link Address
     * @param client {@link AbstractClient}
     */
    static void cache(String url, Client client) {
        if (StringUtils.simpleIsBlank(url) || client == null) {
            return;
        }
        CLIENT_CACHE.putIfAbsent(url, client);
    }

    /**
     * Put the current requested parameters into thread private variable storage
     *
     * @param request Current request parameters
     */
    static <R extends Response> void setCurrentParam(Request<R> request) {
        if (request == null) {
            PARAM_NAMED_SAVER.remove();
        } else {
            PARAM_NAMED_SAVER.set(request);
        }
    }

    /**
     * Get the current thread variable
     *
     * @param <R> Data Generics
     * @return response data
     */
    public static <R extends Response> Request<R> getCurrentParam() {
        return PARAM_NAMED_SAVER.get();
    }

    /**
     * Obtain or initialize the client object
     *
     * @param newClientSupplier New client provider
     * @param request           Request Object Parameters
     * @param uri               Link Address
     * @param <R>               Object Generics
     * @return Client singleton client
     */
    public static <R extends Response> Client<R> getClient(Supplier<Client<R>> newClientSupplier,
                                                           Request<R> request,
                                                           String uri) {
        AssertUtils.Operation.hasText(uri,
                "Api request uri can not be null !");
        AssertUtils.Operation.notNull(request,
                "Api request params can not be null !");
        setCurrentParam(request);
        String url = request.formatUrl(uri);
        Client<R> client = getCacheClient(url);
        if (client == null) {
            synchronized (lock) {
                client = getCacheClient(url);
                if (client == null) {
                    cache(url, newClientSupplier.get());
                    client = getCacheClient(url);
                }
            }
        }
        return client;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String convert(Request<R> request, String responseStr) {
        //Perform JSON conversion according to the return type, and the final processing item is JSON
        if (!request.isrResponseJsonType()) {
            if (request.isrResponseXmlType()) {
                responseStr = request.xmlConvert().apply(responseStr);
            }
        }
        //Special conversion requirements
        if (request.specialConvert() != null) {
            responseStr = request.specialConvert().apply(responseStr);
        }
        return responseStr;
    }

    @Override
    @NonNull
    public R JsonToConvertResponse(Request<R> request, String responseStr) {
        R response;
        JSONValidator jsonValidator = StringUtils.simpleIsBlank(responseStr) ? null : JSONValidator.from(responseStr);
        if (Objects.isNull(jsonValidator) || !jsonValidator.validate()) {
            String jsonData = JSON.toJSONString(DefaultResponse.buildDataErrorResponse(responseStr));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } else if (Objects.equals(JSONValidator.Type.Array, jsonValidator.getType())) {
            List<R> responses = JSONArray.parseArray(responseStr, request.getResponseCls());
            if (!CollectionUtils.isEmpty(responses)) {
                response = responses.get(0);
            } else {
                response = JSON.parseObject(empty_json, request.getResponseCls());
            }
        } else {
            response = JSON.parseObject(responseStr, request.getResponseCls());
        }
        return response;
    }

    @Override
    public void close() {
        setCurrentParam(null);
    }
}
