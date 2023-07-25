package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.SdkException;
import io.github.zpf9705.expiring.util.AbleUtils;
import io.github.zpf9705.expiring.util.HttpUtils;
import org.springframework.util.StopWatch;

import java.util.Map;

/**
 * HTTP based {@link Client} request client
 *
 * @author zpf
 * @since 3.1.0
 */
public class HttpClient<R extends Response> extends AbstractClient<R> {

    private static final long serialVersionUID = -7155604086466276914L;

    /* ******* super Constructs ***********/

    public HttpClient(String url) {
        super(url);
    }

    @Override
    public R request() {
        Request<R> request = getCurrentParam();
        StopWatch stopWatch = new StopWatch();
        //Request Timer
        stopWatch.start();
        R response;
        String authorJson = null;
        String responseStr = null;
        String errorMsg = null;
        Throwable throwable = null;
        try {
            //Verification of necessary parameters
            request.validate();
            //Obtain request body map
            Map<String, Object> paramsMap = request.toParamsMap();
            //Obtain request body JSON data
            authorJson = JSON.toJSONString(paramsMap);
            //Get Request Header
            Map<String, String> headers = request.getHeadMap();
            //requested action
            responseStr = caseRequestMethodDoThat(request, headers, paramsMap, authorJson);
            //Special Type conversion
            responseStr = this.convert(request, responseStr);
            //Process JSON
            response = this.JsonToConvertResponse(request, responseStr);
        } catch (SdkException e) {
            Console.error("Client request fail, apiName={}, error=[{}]",
                    request.matchApi().name(), ExceptionUtil.stacktraceToOneLineString(e));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildResponse(e.getCode(), e.getMsg()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } catch (Exception e) {
            Console.error("Client request fail, apiName={}, error=[{}]",
                    request.matchApi().name(), ExceptionUtil.stacktraceToOneLineString(e));
            throwable = e;
            errorMsg = ExceptionUtil.stacktraceToOneLineString(throwable);
            String jsonData = JSON.toJSONString(DefaultResponse.buildUnknownResponse(e.getMessage()));
            response = JSON.parseObject(jsonData, request.getResponseCls());
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();

            if (throwable == null) {
                String msgFormat = "Request end, name={}, request={}, response={}, time={}ms";
                Console.info(msgFormat, request.matchApi().name(), authorJson, responseStr,
                        totalTimeMillis);
            } else {
                String msgFormat = "Request fail, name={}, request={}, response={}, error={}, time={}ms";
                Console.info(msgFormat, request.matchApi().name(), authorJson, responseStr,
                        errorMsg, totalTimeMillis);
            }
        }
        AbleUtils.close(this);
        return response;
    }

    /**
     * HTTP requests based on different methods
     *
     * @param request    Request parameter encapsulation class
     * @param headers    Header information
     * @param paramsMap  Parameter map
     * @param authorJson Certified JSON
     * @return JSON return value
     */
    public String caseRequestMethodDoThat(Request<R> request,
                                          Map<String, String> headers,
                                          Map<String, Object> paramsMap,
                                          String authorJson) {
        String responseStr;
        String url = getUrl();
        switch (request.matchApi().getRequestMethod()) {
            case GET:
                responseStr = HttpUtils.get(url, headers, paramsMap);
                break;
            case POST:
                responseStr = HttpUtils.post(url, headers, authorJson);
                break;
            case PUT:
                responseStr = HttpUtils.put(url, headers, authorJson);
                break;
            case DELETE:
                responseStr = HttpUtils.delete(url, headers);
                break;
            default:
                responseStr = null;
                break;
        }
        return responseStr;
    }
}
