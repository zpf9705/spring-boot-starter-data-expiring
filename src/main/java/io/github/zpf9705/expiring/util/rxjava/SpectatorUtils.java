package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.logger.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * This tool class is mainly designed to encapsulate the {@link Flowable} of rxJava3 .
 * <p>
 * Mainly for call schemes with return values, without any memory impact on subscription
 * relationships. For those without return values, please refer to {@link Spectator}.
 * For subscription relationship clearing and releasing, please refer to {@link DisposableUtils}
 * <p>
 * The main approach is to implement retry and method conversion during the process of
 * calling the method body, relying on its producer to call our runtime to produce data
 * and send it to consumers waiting for blocking to obtain the return value.
 * <p>
 * If an exception occurs during the process, it will be retried.
 * Additionally, a retry interval is provided, which can be set according to one's
 * own business needs. Here, only producers are planned to produce one data at a time,
 * so a single value is taken away.
 * <p>
 * Generally, in project interface calls, we often use {@link #runWhileTrampoline(Supplier, Class[], int, long, Class)}
 * to synchronize interface calls. This method effectively implements functions such as
 * abnormal retry and retry intermittency. In fact, the method API is not commonly used,
 * but can be combined arbitrarily through {@link #runWhile(Supplier, Class[], int, long, Executor, Executor, Class)}
 *
 * @author zpf
 * @since 3.1.2
 */
public abstract class SpectatorUtils {

    private SpectatorUtils() {
    }

    /**
     * {@link #runWhileTrampoline(Supplier, Class[], int, long, Class)}no convert type clazz
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection . If null, all exceptions will be caught
     * @param <T>                    Specify Generics
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runWhileTrampolineNoConvert(Supplier<T> able,
                                                    Class<? extends Throwable>[] exceptionClasses,
                                                    int retryTimes,
                                                    long exceptionRetryRestMill) {
        return runWhileTrampoline(able, exceptionClasses, retryTimes, exceptionRetryRestMill, null);
    }

    /**
     * This method enables the producer and consumer to execute synchronously on the
     * same thread and is the current main thread, because this method does not set
     * the scheduling Thread pool for consumers and producers
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection . If null, all exceptions will be caught
     * @param <T>                    Specify Generics
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runWhileTrampoline(Supplier<T> able,
                                           Class<? extends Throwable>[] exceptionClasses,
                                           int retryTimes,
                                           long exceptionRetryRestMill,
                                           Class<T> clazz) {
        return runWhile(able, exceptionClasses, retryTimes, exceptionRetryRestMill,
                null, null, clazz);
    }

    /**
     * This method is provided to the producer to schedule the Thread pool.
     * <p>
     * No matter whether it is null or not, consumers follow the producer's Thread pool to consume
     * <pre>
     *     {@code
     *     System.out.println(Thread.currentThread().getName());
     *         Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
     *                     public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                         emitter.onNext("String.valueOf(a)");
     *                         emitter.onComplete();
     *                     }
     *                 }, BackpressureStrategy.DROP)
     *                 .retry(3)
     *                 .subscribeOn(Schedulers.from(CustomThreadPool.getExecutor()))
     *                 .observeOn(Schedulers.trampoline())
     *                 .subscribe(new Consumer<String>() {
     *                     public void accept(String s) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                     }
     *                 });
     *         System.out.println("--------");}
     * </pre>
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection. If null, all exceptions will be caught
     * @param <T>                    Specify Generics
     * @param subscribeExecutor      The Thread pool that is scheduled by the producer's production data.
     *                               If it is null, the current thread is scheduled by default
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runWhileProducerDispatch(Supplier<T> able,
                                                 Class<? extends Throwable>[] exceptionClasses,
                                                 int retryTimes,
                                                 long exceptionRetryRestMill,
                                                 Executor subscribeExecutor,
                                                 Class<T> clazz) {
        return runWhile(able, exceptionClasses, retryTimes, exceptionRetryRestMill, subscribeExecutor,
                null, clazz);
    }

    /**
     * This method provides the consumer with a Thread pool for scheduling.
     * <p>
     * If it is null, it will consume along with the producer thread.
     * <p>
     * Otherwise, it will use the provided Thread pool for scheduling
     * <pre>
     *   {@code
     *   System.out.println(Thread.currentThread().getName());
     *         Disposable disposable = Flowable.create(new FlowableOnSubscribe<String>() {
     *                     public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                         emitter.onNext("String.valueOf(a)");
     *                         emitter.onComplete();
     *                     }
     *                 }, BackpressureStrategy.DROP)
     *                 .retry(3)
     *                 .subscribeOn(Schedulers.trampoline())
     *                 .observeOn(Schedulers.from(CustomThreadPool.getExecutor()))
     *                 .subscribe(new Consumer<String>() {
     *                     public void accept(String s) throws Throwable {
     *                         System.out.println(Thread.currentThread().getName());
     *                     }
     *                 });
     *         System.out.println("--------");}
     * </pre>
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection . If null, all exceptions will be caught
     * @param <T>                    Specify Generics
     * @param observeExecutor        The Thread pool that the consumer consumes and schedules. If it is null,
     *                               the Thread pool that the producer schedules will be used by default
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runWhileConsumerDispatch(Supplier<T> able,
                                                 Class<? extends Throwable>[] exceptionClasses,
                                                 int retryTimes,
                                                 long exceptionRetryRestMill,
                                                 Executor observeExecutor,
                                                 Class<T> clazz) {
        return runWhile(able, exceptionClasses, retryTimes, exceptionRetryRestMill, null,
                observeExecutor, clazz);
    }

    /**
     * Provide the specified exception judgment and retry the specified number of times
     * during method operation, including thread switching based on incoming parameters
     * and ultimately returning the desired generics
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             Retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection . If null, all exceptions will be caught
     * @param <T>                    Specify Generics
     * @param subscribeExecutor      The Thread pool that is scheduled by the producer's production data.
     *                               If it is null, the current thread is scheduled by default
     * @param observeExecutor        The Thread pool that the consumer consumes and schedules. If it is null,
     *                               the Thread pool that the producer schedules will be used by default
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    @SuppressWarnings("unchecked")
    public static <T> T runWhile(Supplier<T> able,
                                 Class<? extends Throwable>[] exceptionClasses,
                                 int retryTimes,
                                 long exceptionRetryRestMill,
                                 Executor subscribeExecutor,
                                 Executor observeExecutor,
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
                .subscribeOn(getSchedulers(subscribeExecutor == null, subscribeExecutor))
                .observeOn(getSchedulers(observeExecutor == null, observeExecutor))
                .retry(retryTimes, (e) -> specifyAnException(exceptionClasses, exceptionRetryRestMill, e.getClass()));
        T t;
        if (clazz != null) {
            t = flowable.ofType(clazz).blockingSingle();
        } else {
            t = (T) flowable.blockingSingle();
        }
        return t;
    }

    /**
     * Verify if the thrown exception is in the specified exception group ,
     * And can set the interval rest millisecond value for abnormal retries
     *
     * @param exceptionClasses       Specify retry exception collection
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value
     * @param specifyClazz           Throw an exception class object
     * @return {@code  True} retry {@code false} non retry
     */
    protected static boolean specifyAnException(Class<? extends Throwable>[] exceptionClasses,
                                                long exceptionRetryRestMill,
                                                @NotNull Class<? extends Throwable> specifyClazz) {
        boolean retry;
        if (ArrayUtils.simpleIsEmpty(exceptionClasses)) {
            // no special exception  retry at now
            retry = true;
        } else {
            //if false prove no special exception so no retry
            retry = Arrays.stream(exceptionClasses).anyMatch(v -> v.isAssignableFrom(specifyClazz));
        }
        //in retry so exceptionRetryTime to sleep
        if (retry) {
            if (exceptionRetryRestMill == 0) {
                //rest no time
                return true;
            }
            try {
                Console.info("When retry there sleep {} millis ", exceptionRetryRestMill);
                //------------------------------------
                TimeUnit.MILLISECONDS.sleep(exceptionRetryRestMill);
            } catch (InterruptedException ex) {
                // if Interrupted try to retry
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Switch between main thread and self thread according to whether the main thread
     * pool execution parameter is used {@link Schedulers} {@link Scheduler}
     *
     * @param trampoline Whether to run on the main thread
     * @param executor   The Thread pool provided for the self thread must be threaded when the self thread runs
     * @return {@code Scheduler} default to {@link Schedulers#trampoline()} if no executor default to {@link ForkJoinPool}
     */
    protected static Scheduler getSchedulers(boolean trampoline, Executor executor) {
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
