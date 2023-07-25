package io.github.zpf9705.expiring.spring_jdk.example_sdk;

import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.expiring.util.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;


/**
 * API Exception Assertion
 *
 * @author zpf
 * @since 3.1.0
 */
public final class SdkAssert {

    private static final Integer ASSERT_UTILS_THROW_EX_CODE = 58845;

    public static void isTrue(boolean expression, @NonNull String message) {
        if (!expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void isFalse(boolean expression, @NonNull String message) {
        if (expression) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void isNull(@Nullable Object object, @NonNull String message) {
        if (Objects.nonNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void notNull(@Nullable Object object, @NonNull String message) {
        if (Objects.isNull(object)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void hasText(@Nullable String text, @NonNull String message) {
        if (StringUtils.simpleIsBlank(text)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, @NonNull String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void notEmpty(@Nullable Object[] array, @NonNull String message) {
        if (ArrayUtil.isEmpty(array)) {
            throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
        }
    }

    public static void throwException(String message) {
        throw new SdkException(ASSERT_UTILS_THROW_EX_CODE, message);
    }

    public static void throwException(int code, String message) {
        throw new SdkException(code, message);
    }
}
