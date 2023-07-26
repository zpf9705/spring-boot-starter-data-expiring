package io.github.zpf9705.expiring.spring_jdk.example_sdk.annotation;

import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;

import java.lang.annotation.*;

/**
 * When using map for JSON parameter conversion, it is an alias for key value conversion
 * <p>
 * {@link Request#toParamsMap()}
 *
 * @author zpf
 * @since 3.1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapFiled {

    /**
     * Alias for key
     *
     * @return not be {@literal null}
     */
    String name();
}
