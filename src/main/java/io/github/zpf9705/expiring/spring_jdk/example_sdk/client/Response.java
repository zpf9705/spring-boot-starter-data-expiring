package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import java.io.Serializable;

/**
 * The standardized interface of the request response class defines the success scheme of the response,
 * exception information acquisition, and JSON as the final serialization scheme
 *
 * @author zpf
 * @since 3.1.0
 */
public interface Response extends Serializable {

    /**
     * Verify if successful
     *
     * @return True Success False Failure
     */
    boolean isSuccess();

    /**
     * Get Request msg
     *
     * @return {@link String}
     */
    String getMessage();

    /**
     * Response result conversion JSON string
     *
     * @return {@link com.alibaba.fastjson.JSON}
     */
    String toJson();
}
