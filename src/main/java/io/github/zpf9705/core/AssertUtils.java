package io.github.zpf9705.core;

import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Exception assertion utils types of {@link OperationsException} and {@link PersistenceException}
 *
 * @author zpf
 * @since 2.1.1-complete
 */
public abstract class AssertUtils {

    /**
     * @see OperationsException
     */
    public abstract static class Operation {

        public static void isTrue(boolean expression, @NonNull String message) {
            AssertUtils.isTrue(expression, () -> newOperationException(message));
        }

        public static void notNull(@Nullable Object object, @NonNull String message) {
            AssertUtils.notNull(object, () -> newOperationException(message));
        }

        public static void hasText(@Nullable String text, @NonNull String message) {
            AssertUtils.hasText(text, () -> newOperationException(message));
        }

        public static void notEmpty(@Nullable Collection<?> collection, @NonNull String message) {
            AssertUtils.notEmpty(collection, () -> newOperationException(message));
        }

        public static void notEmpty(@Nullable Object[] array, @NonNull String message) {
            AssertUtils.notEmpty(array, () -> newOperationException(message));
        }

        @NonNull
        private static OperationsException newOperationException(@NonNull String message) {
            return new OperationsException(message);
        }
    }

    /**
     * @see PersistenceException
     */
    public abstract static class Persistence {

        public static void isTrue(boolean expression, @NonNull String message) {
            AssertUtils.isTrue(expression, () -> newPersistenceException(message));
        }

        public static void notNull(@Nullable Object object, @NonNull String message) {
            AssertUtils.notNull(object, () -> newPersistenceException(message));
        }

        public static void hasText(@Nullable String text, @NonNull String message) {
            AssertUtils.hasText(text, () -> newPersistenceException(message));
        }

        public static void notEmpty(@Nullable Collection<?> collection, @NonNull String message) {
            AssertUtils.notEmpty(collection, () -> newPersistenceException(message));
        }

        public static void notEmpty(@Nullable Object[] array, @NonNull String message) {
            AssertUtils.notEmpty(array, () -> newPersistenceException(message));
        }

        @NonNull
        private static PersistenceException newPersistenceException(@NonNull String message) {
            return new PersistenceException(message);
        }
    }

    /**
     * Assertion method of isTrue
     *
     * @param expression Validation Expression
     * @param supplier   exception supplier
     */
    private static void isTrue(boolean expression, @NonNull Supplier<ExpiringException> supplier) {
        if (!expression) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of notNull
     *
     * @param object   obj checker
     * @param supplier exception supplier
     */
    private static void notNull(@Nullable Object object, @NonNull Supplier<ExpiringException> supplier) {
        if (Objects.isNull(object)) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of hasText
     *
     * @param text     str
     * @param supplier exception supplier
     */
    private static void hasText(@Nullable String text, @NonNull Supplier<ExpiringException> supplier) {
        if (!StringUtils.isNotBlank(text)) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of notEmpty [collection]
     *
     * @param collection list/set/...
     * @param supplier   exception supplier
     */
    private static void notEmpty(@Nullable Collection<?> collection, @NonNull Supplier<ExpiringException> supplier) {
        if (CollectionUtils.isEmpty(collection)) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of notEmpty [array]
     *
     * @param array    array
     * @param supplier exception supplier
     */
    private static void notEmpty(@Nullable Object[] array, @NonNull Supplier<ExpiringException> supplier) {
        if (ArrayUtil.isEmpty(array)) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of throwException
     *
     * @param supplier exception supplier
     */
    private static void throwException(@NonNull Supplier<ExpiringException> supplier) {
        throw supplier.get();
    }
}
