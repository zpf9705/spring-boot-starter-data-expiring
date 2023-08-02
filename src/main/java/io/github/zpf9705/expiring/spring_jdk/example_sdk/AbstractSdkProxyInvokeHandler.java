package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.AbstractRequestParams;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;
import io.github.zpf9705.expiring.spring_jdk.support.AbstractJdkProxySupport;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.lang.reflect.Method;

/**
 * The unified parameter transformation processing of jdk proxy object method calls
 * abstracts and supports the class, and ultimately hands it over to the real processing class
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractSdkProxyInvokeHandler<T> extends AbstractJdkProxySupport<T> {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        AssertUtils.Operation.notEmpty(args, "Sdk way [" + method.getName() + "] this call no args");
        //This requires a single model parameter
        Object arg = args[0];
        //Determine if it is of type AbstractRequestParams
        AssertUtils.Operation.isTrue((arg instanceof AbstractRequestParams),
                "Sdk way [" + method.getName() + "]  args no qualified");
        //Execute call API
        return doSdk((Request<?>) arg, method.getName(), method.getReturnType());
    }

    /**
     * Pass parameters to execute the API and provide the call method name (logging) and response type
     *
     * @param request      request parameters {@link Request}
     * @param methodName   method Name {@link SdkEnum#name()}
     * @param responseType final convert response class type
     * @return Sdk return value for {@link io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Response}
     */
    public abstract Object doSdk(Request<?> request, String methodName, Class<?> responseType) throws SdkException;
}
