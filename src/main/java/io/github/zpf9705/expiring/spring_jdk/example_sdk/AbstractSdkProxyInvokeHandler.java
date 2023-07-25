package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.AbstractRequestParams;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;
import io.github.zpf9705.expiring.spring_jdk.support.AbstractJdkProxySupport;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Public processing class for proxy object method execution
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractSdkProxyInvokeHandler<T> extends AbstractJdkProxySupport<T> implements Serializable,
        SdkExecutor {

    private static final long serialVersionUID = -6526921211944104635L;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        AssertUtils.Operation.notEmpty(args, "Sdk way [" + method.getName() + "] this call no args");
        //This requires a single model parameter
        Object arg = args[0];
        //Determine if it is of type AbstractRequestParams
        AssertUtils.Operation.isTrue((arg instanceof AbstractRequestParams),
                "Sdk way [" + method.getName() + "]  args no qualified");
        //Execute call API
        return execute((Request<?>) arg, method.getName(), method.getReturnType());
    }

    @Override
    @NonNull
    public abstract Object execute(Request<?> request, String methodName, Class<?> returnType) throws SdkException;
}
