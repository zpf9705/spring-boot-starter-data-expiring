package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;
import org.springframework.lang.NonNull;

/**
 * Sdk Execution Method Interface
 *
 * @author zpf
 * @since 3.1.0
 */
@FunctionalInterface
public interface SdkExecutor {

    /**
     * Execute API Request Method
     *
     * @param request    Request parameters {@link Request}
     * @param methodName API Method Name {@link SdkEnum}
     * @param returnType convert class type
     * @return API return value
     */
    @NonNull
    Object execute(Request<?> request, String methodName, Class<?> returnType) throws SdkException;
}
