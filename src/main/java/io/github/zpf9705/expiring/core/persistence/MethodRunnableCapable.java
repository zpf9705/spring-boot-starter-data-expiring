package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.util.function.Consumer;

/**
 * Method runnable choose for {@link Runnable}
 *
 * @author zpf
 * @since 3.0.0
 */
public interface MethodRunnableCapable {

    /**
     * Runnable with a {@code persistence method}
     *
     * @param runnable            {@link Runnable}
     * @param errorLoggerConsumer Persistence method error logger
     */
    void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer);
}
