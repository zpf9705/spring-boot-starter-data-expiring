package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;


/**
 * Asynchronous or synchronous blocking subscription tool，Mainly relying on him for implementation {@link Flowable}
 * <p>
 * Functions such as exception retry, generic conversion, and thread switching for method calls
 *
 * @author zpf
 * @since jdk.spi.version-2022.11.18 - [2022-08-17 16:52]
 */
public final class SpectatorUtils {

    private SpectatorUtils() {
    }

    /**
     * {@link #runWithRetry2ExceptionAll(Supplier, boolean, Class)} no provider clazz use method return value type
     *
     * @param able Method Run Function , must no be {@literal null}
     * @param <T>  Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry2ExceptionAll(Supplier<T> able,
                                                  boolean trampoline) {
        return runWithRetry2ExceptionAll(able, trampoline, null);
    }

    /**
     * {@link #runWithRetry2DefaultExecutor(Supplier, Class[], boolean, Class)} No need to specify exceptions
     * All exceptions are retried
     *
     * @param able  Method Run Function , must no be {@literal null}
     * @param clazz Return value conversion clazz object ,
     *              The default is the type of value returned by the method
     * @param <T>   Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry2ExceptionAll(Supplier<T> able,
                                                  boolean trampoline,
                                                  Class<T> clazz) {
        return runWithRetry2DefaultExecutor(able, null, trampoline, clazz);
    }

    /**
     * {@link #runRetry2While(Supplier, Class[], boolean, Executor, Class)} use default executor
     *
     * @param exceptionClasses Specify retry exception collection
     * @param able             Method Run Function , must no be {@literal null}
     * @param clazz            Return value conversion clazz object ,
     *                         The default is the type of value returned by the method
     * @param <T>              Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry2DefaultExecutor(Supplier<T> able,
                                                     Class<? extends Throwable>[] exceptionClasses,
                                                     boolean trampoline,
                                                     Class<T> clazz) {
        return runRetry2While(able, exceptionClasses, trampoline, null, clazz);
    }

    /**
     * {@link #runWhile(Supplier, Class[], int, boolean, Executor, Class)} Specify two retries
     *
     * @param able             Method Run Function , must no be {@literal null}
     * @param exceptionClasses Specify retry exception collection
     * @param <T>              Specify Generics
     * @param executor         Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @param clazz            Return value conversion clazz object ,
     *                         The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runRetry2While(Supplier<T> able,
                                       Class<? extends Throwable>[] exceptionClasses,
                                       boolean trampoline,
                                       Executor executor,
                                       Class<T> clazz) {
        return runWhile(able, exceptionClasses, 2, trampoline, executor, clazz);
    }


    /**
     * {@link #runWhile(Supplier, Class[], int, boolean, Executor, Class)} no type clazz
     *
     * @param able             Method Run Function , must no be {@literal null}
     * @param retryTimes       retry count default to {@code 1}
     * @param exceptionClasses Specify retry exception collection
     * @param <T>              Specify Generics
     * @param executor         Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @return Specify conversion type object
     */
    public static <T> T runNoTypeClazzWhile(Supplier<T> able,
                                            Class<? extends Throwable>[] exceptionClasses,
                                            int retryTimes,
                                            boolean trampoline,
                                            Executor executor) {
        return runWhile(able, exceptionClasses, retryTimes, trampoline, executor, null);
    }

    /**
     * Provide the specified exception judgment and retry the specified number of times
     * during method operation, including thread switching based on incoming parameters
     * and ultimately returning the desired generics
     *
     * @param able             Method Run Function , must no be {@literal null}
     * @param retryTimes       retry count default to {@code 1}
     * @param exceptionClasses Specify retry exception collection
     * @param <T>              Specify Generics
     * @param executor         Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @param clazz            Return value conversion clazz object ,
     *                         The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    @SuppressWarnings("unchecked")
    public static <T> T runWhile(Supplier<T> able,
                                 Class<? extends Throwable>[] exceptionClasses,
                                 int retryTimes,
                                 boolean trampoline,
                                 Executor executor,
                                 Class<T> clazz) {
        //must of able and clazz
        if (able == null) {
            return null;
        }
        //default to 1
        if (retryTimes == 0) retryTimes = 1;
        Flowable<Object> flowable = Flowable.create(v -> {
                    v.onNext(able.get());
                    v.onComplete();
                }, BackpressureStrategy.LATEST)
                .observeOn(getSchedulers(trampoline, executor))
                .retry(retryTimes, (e) -> specifyAnException(exceptionClasses, e.getClass()));
        T t;
        if (clazz != null) {
            t = flowable.ofType(clazz).blockingSingle();
        } else {
            t = (T) flowable.blockingSingle();
        }
        return t;
    }

    /**
     * Verify if the thrown exception is in the specified exception group
     *
     * @param exceptionClasses Specify retry exception collection
     * @param specifyClazz     Throw an exception class object
     * @return {@code  True} retry {@code false} non retry
     */
    private static boolean specifyAnException(Class<? extends Throwable>[] exceptionClasses,
                                              @NotNull Class<? extends Throwable> specifyClazz) {
        if (ArrayUtils.simpleIsEmpty(exceptionClasses)) {
            // no special exception  retry at now
            return true;
        }
        return Arrays.stream(exceptionClasses).anyMatch(v -> v.isAssignableFrom(specifyClazz));
    }

    /**
     * Switch between main thread and self thread according to whether the main thread
     * pool execution parameter is used {@link Schedulers} {@link Scheduler}
     *
     * @param trampoline Whether to run on the main thread
     * @param executor   The Thread pool provided for the self thread must be threaded when the self thread runs
     * @return {@code Scheduler} default to {@link Schedulers#trampoline()} if no executor default to {@link ForkJoinPool}
     */
    private static Scheduler getSchedulers(boolean trampoline, Executor executor) {
        Scheduler scheduler;
        if (trampoline) {
            scheduler = Schedulers.trampoline();
        } else {
            if (executor != null) {
                scheduler = Schedulers.from(executor);
            } else {
                //default main thread to run
                scheduler = Schedulers.from(ForkJoinPool.commonPool());
            }
        }
        return scheduler;
    }
}
