package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import com.alibaba.fastjson.JSON;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.ClientUtils;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Response;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;

/**
 * Inject proxy classes, define the final processing scheme,
 * and call different API services based on different 【 uri/input parameter objects 】
 *
 * @author zpf
 * @since 3.1.0
 */
public class SdkProxyBeanDefinition<T> extends AbstractSdkProxyInvokeHandler<T> {

    private static final long serialVersionUID = -4976006670359451017L;

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    @NonNull
    public Object execute(Request<?> request, String methodName, Class<?> returnType) throws SdkException {
        AssertUtils.Operation.notNull(request, "requestParams no be null");
        AssertUtils.Operation.notNull(methodName, "name no be null");
        AssertUtils.Operation.notNull(returnType, "returnType no be null");
        Response response;
        try {
            response = ClientUtils.execute(this.uri, request);
        } catch (Throwable e) {
            //If the parameter preparation before execution throws an exception, it will be directly thrown here
            throw new SdkException(400, methodName + "param prepare no enough [" + e.getMessage() + "]");
        }
        return JSON.parseObject(response.toJson(), returnType);
    }
}
