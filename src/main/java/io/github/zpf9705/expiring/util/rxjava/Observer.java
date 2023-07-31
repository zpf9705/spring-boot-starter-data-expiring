package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.StringUtils;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Method call inspector interface , abnormal situation of monitoring method operation
 * <p>
 * Utilizing the {@link Flowable} specific properties of rxjava3
 * <p>
 * Need to select appropriate back pressure parameters for its operation，It can be understood
 * as the handling strategy when the {@link BackpressureStrategy} upstream and downstream speeds
 * are inconsistent, You can also go to specific websites to learn more
 * <a href="https://www.dazhuanlan.com/unclebill/topics/1173554">BackpressureStrategy introduce</a>
 *
 * @author zpf
 * @since 3.1.0
 */
public interface Observer<T> {

    /**
     * Choose an appropriate backpressure {@link BackpressureStrategy} strategy for the operating method
     *
     * @return no be {@literal null}
     */
    @NotNull
    BackpressureStrategy selectBack();

    /**
     * Select the appropriate Thread pool {@link Executor} for the run method
     * If null, run in the main thread
     *
     * @return no be {@literal null}
     */
    @CanNull
    default Executor getExecutor() {
        return null;
    }

    /**
     * Set up exception format functions
     *
     * @return {@link Consumer}
     */
    @NotNull
    default Function<Throwable, String> formatThrowFunction() {
        return Throwable::getMessage;
    }

    /**
     * Obtain the custom retry count. If it is not set in the system {@link io.github.zpf9705.expiring.util.SystemUtils},
     * take it from the system. If it is not set in the system, it defaults to {@code 2} retries
     *
     * @return retry times default {@code 2}
     */
    int getRetryTimes();

    /**
     * Wait time for thread retry when an exception occurs
     *
     * @return wait times and Unit in milliseconds
     */
    long exceptionRetryRestTime();

    /**
     * Specify special retry exceptions. If left blank, all exceptions can be retried
     *
     * @return exception array
     */
    @CanNull
    Class<? extends Throwable>[] specialRetry();

    /**
     * Utilizing the Running Mechanism of rxJava {@link Flowable} , based on the provided operating parameters
     * <p>
     * Implementation method operation, interface check, information collection, exception retry
     * If the retry count {@link #getRetryTimes()} is exceeded, the last exception will be thrown
     *
     * @param run              Run Method Provider
     * @param type             Success condition assertion
     * @param check            Operational condition assertion
     * @param simpleMsgHandler Exception occurrence collection function
     * @return {@link Flowable}
     */
    @NotNull
    default Flowable<T> run(Supplier<T> run,
                            Class<T> type,
                            Predicate<T> check,
                            Function<T, String> simpleMsgHandler) {
        return Flowable.create(click -> {
                    click.onNext(checkValue(run, check, simpleMsgHandler));
                    click.onComplete();
                }, selectBack())
                //new thread to run
                .observeOn(getExecutor() != null ?
                        Schedulers.from(getExecutor()) :
                        Schedulers.trampoline())
                //retry times no need assign ex
                .retry(getRetryTimes(), this::retryWhen)
                //change type
                .ofType(type);
    }

    /**
     * When a method throws an exception during runtime, it can be determined
     * whether it is a specified exception {@link #specialRetry()} based on the provided exception queue
     * to determine whether it is a retry. If {@link #exceptionRetryRestTime()}the interval between exception
     * retries is provided, the thread will retry after resting for these times
     *
     * @param e Exception thrown by method
     * @return if {@code true} retry or no retry
     * @since 3.1.2
     */
    default boolean retryWhen(Throwable e) {
        Class<? extends Throwable>[] exClasses = specialRetry();
        boolean retry;
        //if retry exceptions nonnull check e in it
        if (exClasses != null) {
            //if false prove no special exception so no retry
            retry = SpectatorUtils.specifyAnException(exClasses, e.getClass());
        } else {
            //if null retry all exception
            retry = true;
        }
        //in retry so exceptionRetryTime to sleep
        if (retry) {
            long sleep = exceptionRetryRestTime();
            if (sleep == 0) {
                //rest no time
                return true;
            }
            try {
                Console.info("When retry there sleep {} millis ", sleep);
                //------------------------------------
                TimeUnit.MILLISECONDS.sleep(sleep);
            } catch (InterruptedException ex) {
                // if Interrupted try to retry
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Handle exceptions uniformly, throw and give rxjava3 for retry
     * <p>
     * Retries for compatibility exceptions and non success calls
     *
     * @param run              Run Method Provider {@link Supplier}
     * @param check            Success condition assertion
     * @param simpleMsgHandler Exception occurrence collection function
     * @return Recycling objects
     */
    default T checkValue(Supplier<T> run,
                         Predicate<T> check,
                         Function<T, String> simpleMsgHandler) {
        T t = null;
        String esg = null;
        try {
            //there is also as run way
            t = run.get();
            boolean test = check.test(t);
            //check no pass give ex
            if (!test) {
                // get no checked pass error msg
                esg = simpleMsgHandler.apply(t);
            }
        } catch (Throwable e) {
            // solve exception
            esg = formatThrowFunction().apply(e);
        }
        //if have msg , so throw it with retry
        if (StringUtils.simpleNotBlank(esg)) {
            throw new IllegalArgumentException(esg);
        }
        return t;
    }
}
