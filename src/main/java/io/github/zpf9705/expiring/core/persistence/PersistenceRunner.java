package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Cache persistence methods of operation indicators, decided to take what kind of way to run
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class PersistenceRunner implements MethodRunnableCapable {

    private static final boolean async;

    private static volatile MethodRunnableCapable capable;

    static {
        async = Configuration.getConfiguration().getPersistenceAsync();
    }

    /**
     * Get a Singleton {@code MethodRunnableCapable}
     * <ul>
     *     <li>{@link ASyncPersistenceRunner}</li>
     *     <li>{@link SyncPersistenceRunner}</li>
     * </ul>
     *
     * @return {@link MethodRunnableCapable}
     */
    public static MethodRunnableCapable getCapable() {
        if (capable == null) {
            synchronized (PersistenceRunner.class) {
                if (capable == null) {
                    if (async) {
                        capable = new ASyncPersistenceRunner();
                    } else {
                        capable = new SyncPersistenceRunner();
                    }
                }
            }
        }
        return capable;
    }

    public abstract void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer);


    /**
     * Sync to run Persistence method
     */
    private static class SyncPersistenceRunner extends PersistenceRunner {

        @Override
        public void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer) {
            try {
                runnable.run();
            } catch (Throwable e) {
                errorLoggerConsumer.accept(e.getMessage());
            }

        }
    }

    /**
     * ASync to run Persistence method
     */
    private static class ASyncPersistenceRunner extends PersistenceRunner {

        @Override
        public void run(@NotNull Runnable runnable, @NotNull Consumer<String> errorLoggerConsumer) {
            CompletableFuture.runAsync(runnable)
                    .whenComplete((s, e) -> errorLoggerConsumer.accept(e.getMessage()));
        }
    }
}
