package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.example_sdk.client.Request;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.StringUtils;

import java.util.Collection;
import java.util.Objects;


/**
 * SDK encapsulation specific assertion validator.
 * <p>
 * Mainly aimed at verifying necessary parameters before{@link Request#validate()} operation
 *
 * @author zpf
 * @since 3.1.0
 */
public final class SdkAssert {

    private static final Integer ASSERT_UTILS_THROW_EX_CODE = 58845;

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isTrue(boolean expression, @NotNull String message) {
        if (!expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isFalse(boolean expression, @NotNull String message) {
        if (expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void isNull(@CanNull Object object, @NotNull String message) {
        if (Objects.nonNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notNull(@CanNull Object object, @NotNull String message) {
        if (Objects.isNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void hasText(@CanNull String text, @NotNull String message) {
        if (StringUtils.simpleIsBlank(text)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notEmpty(@CanNull Collection<?> collection, @NotNull String message) {
        if (CollectionUtils.simpleIsEmpty(collection)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void notEmpty(@CanNull Object[] array, @NotNull String message) {
        if (ArrayUtils.simpleIsEmpty(array)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void throwException(String message) {
        throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.util.AssertUtils.Operation
     */
    public static void throwException(int code, String message) {
        throw new SdkException(code, message);
    }
}
