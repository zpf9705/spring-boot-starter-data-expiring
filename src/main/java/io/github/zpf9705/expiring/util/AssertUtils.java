package io.github.zpf9705.expiring.util;

import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.expiring.core.ExpiringException;
import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import org.apache.commons.lang3.StringUtils;

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

        public static void isTrue(boolean expression, @NotNull String message) {
            AssertUtils.isTrue(expression, () -> newOperationException(message));
        }

        public static void notNull(@CanNull Object object, @NotNull String message) {
            AssertUtils.notNull(object, () -> newOperationException(message));
        }

        public static void hasText(@CanNull String text, @NotNull String message) {
            AssertUtils.hasText(text, () -> newOperationException(message));
        }

        public static void notEmpty(@CanNull Collection<?> collection, @NotNull String message) {
            AssertUtils.notEmpty(collection, () -> newOperationException(message));
        }

        public static void notEmpty(@CanNull Object[] array, @NotNull String message) {
            AssertUtils.notEmpty(array, () -> newOperationException(message));
        }

        @NotNull
        private static OperationsException newOperationException(@NotNull String message) {
            return new OperationsException(message);
        }
    }

    /**
     * @see PersistenceException
     */
    public abstract static class Persistence {

        public static void isTrue(boolean expression, @NotNull String message) {
            AssertUtils.isTrue(expression, () -> newPersistenceException(message));
        }

        public static void notNull(@CanNull Object object, @NotNull String message) {
            AssertUtils.notNull(object, () -> newPersistenceException(message));
        }

        public static void hasText(@CanNull String text, @NotNull String message) {
            AssertUtils.hasText(text, () -> newPersistenceException(message));
        }

        public static void notEmpty(@CanNull Collection<?> collection, @NotNull String message) {
            AssertUtils.notEmpty(collection, () -> newPersistenceException(message));
        }

        public static void notEmpty(@CanNull Object[] array, @NotNull String message) {
            AssertUtils.notEmpty(array, () -> newPersistenceException(message));
        }

        @NotNull
        private static PersistenceException newPersistenceException(@NotNull String message) {
            return new PersistenceException(message);
        }
    }

    /**
     * Assertion method of isTrue
     *
     * @param expression Validation Expression
     * @param supplier   exception supplier
     */
    private static void isTrue(boolean expression, @NotNull Supplier<ExpiringException> supplier) {
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
    private static void notNull(@CanNull Object object, @NotNull Supplier<ExpiringException> supplier) {
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
    private static void hasText(@CanNull String text, @NotNull Supplier<ExpiringException> supplier) {
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
    private static void notEmpty(@CanNull Collection<?> collection, @NotNull Supplier<ExpiringException> supplier) {
        if (collection == null || collection.isEmpty()) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of notEmpty [array]
     *
     * @param array    array
     * @param supplier exception supplier
     */
    private static void notEmpty(@CanNull Object[] array, @NotNull Supplier<ExpiringException> supplier) {
        if (ArrayUtil.isEmpty(array)) {
            throw supplier.get();
        }
    }

    /**
     * Assertion method of throwException
     *
     * @param supplier exception supplier
     */
    private static void throwException(@NotNull Supplier<ExpiringException> supplier) {
        throw supplier.get();
    }
}
