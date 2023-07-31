package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.SystemUtils;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * Asynchronous or synchronous blocking subscription tool，Mainly relying on him for implementation {@link Flowable}
 * <p>
 * Functions such as exception retry, generic conversion, and thread switching for method calls
 *
 * @author zpf
 * @since 3.1.2
 */
public abstract class SpectatorUtils {

    protected static final List<Disposable> dis = new CopyOnWriteArrayList<>();

    protected static final ThreadFactory default_thread_factory = new ThreadFactory() {

        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(0);

        public Thread newThread(@NotNull Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            thread.setName("disposable-clear-thread-" + threadNumber.getAndIncrement());
            return thread;
        }
    };

    public static final String core_size_sign = "disposable.clear.core.thead.size";

    public static final String start_init_delay = "disposable.clear.start.init.delay";

    public static final String start_period = "disposable.clear.start.period";

    public static final String timeunit = "disposable.clear.start.timeunit";

    public static final ScheduledExecutorService service;

    static {

        //ScheduledExecutor init
        service = Executors.newScheduledThreadPool(
                SystemUtils.getPropertyWithConvert(core_size_sign, Integer::parseInt, 1),
                default_thread_factory);

        start();
    }

    private SpectatorUtils() {
    }

    /**
     * {@link #runWithRetry2_0ExceptionAll(Supplier, boolean, Class)} no provider clazz use method return value type
     *
     * @param able       Method Run Function , must no be {@literal null}
     * @param trampoline Whether to run on the main thread
     * @param <T>        Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry20ExceptionAll(Supplier<T> able,
                                                   boolean trampoline) {
        return runWithRetry2_0ExceptionAll(able, trampoline, null);
    }

    /**
     * {@link #runWithRetry2_0DefaultExecutor(Supplier, Class[], boolean, Class)} No need to specify exceptions
     * All exceptions are retried
     *
     * @param able       Method Run Function , must no be {@literal null}
     * @param clazz      Return value conversion clazz object ,
     *                   The default is the type of value returned by the method
     * @param trampoline Whether to run on the main thread
     * @param <T>        Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry2_0ExceptionAll(Supplier<T> able,
                                                    boolean trampoline,
                                                    Class<T> clazz) {
        return runWithRetry2_0DefaultExecutor(able, null, trampoline, clazz);
    }

    /**
     * {@link #runRetry2While(Supplier, Class[], long, boolean, Executor, Class)} use default executor
     * and no exception retry interval millisecond
     *
     * @param exceptionClasses Specify retry exception collection
     * @param able             Method Run Function , must no be {@literal null}
     * @param trampoline       Whether to run on the main thread
     * @param clazz            Return value conversion clazz object ,
     *                         The default is the type of value returned by the method
     * @param <T>              Specify Generics
     * @return Specify conversion type object
     */
    public static <T> T runWithRetry2_0DefaultExecutor(Supplier<T> able,
                                                       Class<? extends Throwable>[] exceptionClasses,
                                                       boolean trampoline,
                                                       Class<T> clazz) {
        return runRetry2While(able, exceptionClasses, 0, trampoline, null, clazz);
    }

    /**
     * {@link #runWhile(Supplier, Class[], int, long, boolean, Executor, Class)} Specify two retries
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param exceptionClasses       Specify retry exception collection
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param <T>                    Specify Generics
     * @param trampoline             Whether to run on the main thread
     * @param executor               Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    public static <T> T runRetry2While(Supplier<T> able,
                                       Class<? extends Throwable>[] exceptionClasses,
                                       long exceptionRetryRestMill,
                                       boolean trampoline,
                                       Executor executor,
                                       Class<T> clazz) {
        return runWhile(able, exceptionClasses, 2, exceptionRetryRestMill, trampoline, executor, clazz);
    }


    /**
     * {@link #runWhile(Supplier, Class[], int, long, boolean, Executor, Class)} no type clazz .
     * <pre>
     *     {@code String s =
     *     SpectatorUtils.runNoTypeClazzWhile(new Supplier<String>() {
     *                                  public String get() {
     *                                        System.out.println("1111");
     *                                        return String.valueOf(1 / 0);}},
     *                                  new Class[]{NullPointerException.class
     *                                  ,ArithmeticException.class},2, 1000L, true, null);}
     * </pre>
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection
     * @param <T>                    Specify Generics
     * @param trampoline             Whether to run on the main thread
     * @param executor               Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @return Specify conversion type object
     */
    public static <T> T runNoTypeClazzWhile(Supplier<T> able,
                                            Class<? extends Throwable>[] exceptionClasses,
                                            int retryTimes,
                                            long exceptionRetryRestMill,
                                            boolean trampoline,
                                            Executor executor) {
        return runWhile(able, exceptionClasses, retryTimes, exceptionRetryRestMill, trampoline, executor, null);
    }

    /**
     * Provide the specified exception judgment and retry the specified number of times
     * during method operation, including thread switching based on incoming parameters
     * and ultimately returning the desired generics
     *
     * @param able                   Method Run Function , must no be {@literal null}
     * @param retryTimes             retry count default to {@code 1}
     * @param exceptionRetryRestMill Abnormal retry interval millisecond value default to {@code 0}
     * @param exceptionClasses       Specify retry exception collection
     * @param trampoline             Whether to run on the main thread
     * @param <T>                    Specify Generics
     * @param executor               Switch Thread pool required by self thread default to {@link ForkJoinPool}
     * @param clazz                  Return value conversion clazz object ,
     *                               The default is the type of value returned by the method
     * @return Specify conversion type object
     */
    @SuppressWarnings("unchecked")
    public static <T> T runWhile(Supplier<T> able,
                                 Class<? extends Throwable>[] exceptionClasses,
                                 int retryTimes,
                                 long exceptionRetryRestMill,
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
                .retry(retryTimes, (e) -> specifyAnException(exceptionClasses,
                        exceptionRetryRestMill, e.getClass()));
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

    /**
     * Pre add subscription relationships to static favorites, to be processed when confirmed
     *
     * @param disposable {@link Disposable}
     */
    public static void addDisposable(Disposable disposable) {
        if (disposable != null) {
            dis.add(disposable);
        }
    }

    /**
     * Clear the subscription relationship, free the occupied memory, and avoid Memory leak
     */
    protected static void clearDisposable() {
        if (dis.isEmpty()) {
            return;
        }
        Console.info("start clean up disposable");
        //To prevent scheduled tasks from starting and clearing subscriptions that have not been completed,
        // add the currently completed subscriptions to a new list for execution first
        List<Disposable> solveDisposables = new CopyOnWriteArrayList<>(dis);
        solveDisposables.forEach(Disposable::dispose);
        //Delete completed
        dis.removeAll(solveDisposables);
    }

    /**
     * Startup clear Disposable Executor
     */
    private static void start(){
        //startup ScheduledExecutor
        service.scheduleAtFixedRate(SpectatorUtils::clearDisposable,
                SystemUtils.getPropertyWithConvert(start_init_delay, Integer::parseInt, 2),
                SystemUtils.getPropertyWithConvert(start_period, Integer::parseInt, 2),
                SystemUtils.getPropertyWithConvert(timeunit, TimeUnit::valueOf, TimeUnit.MINUTES));
    }

    /**
     * loading of this class
     */
    public static void preload() {
        //no op
    }
}
