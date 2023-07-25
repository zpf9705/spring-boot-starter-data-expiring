package io.github.zpf9705.expiring.spring_jdk.example_sdk.client;

import java.io.Serializable;

/**
 * Define response nodes
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
