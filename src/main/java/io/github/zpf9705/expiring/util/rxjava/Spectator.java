package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.SystemUtils;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The implementation subject of the prosecutor mode method operation
 * <p>
 * The relevant operational parameter definitions for {@link Flowable} are completed here
 * <p>
 * The specific application method of the above parameters can be queried
 * {@link Observer#run(Supplier, Class, Predicate, Function)}
 * <pre>
 *     {@code
 *      Spectator.prepare(new Supplier<Integer>() {
 *                     public Integer get() {
 *                         System.out.println("11111");
 *                         return 1 / 0;
 *                     }
 *                 }, Integer.class, new Predicate<Integer>() {
 *                     public boolean test(Integer integer) {
 *                         return integer != null;
 *                     }
 *                 }, new Function<Integer, String>() {
 *                     public String apply(Integer integer) {
 *                         return integer.toString();
 *                     }
 *                 })
 *                 .exceptionRetryTime(3000L)
 *                 .specialRetry(new Class[]{IllegalArgumentException.class})
 *                 .run()
 *                 .accept(new io.reactivex.rxjava3.functions.Consumer<Integer>() {
 *                     public void accept(Integer integer) throws Throwable {
 *
 *                     }
 *                 }, new io.reactivex.rxjava3.functions.Consumer<Throwable>() {
 *                     public void accept(Throwable throwable) throws Throwable {
 *                         System.out.println(throwable.getMessage());
 *                     }
 *                 });}
 * </pre>
 *
 * @author zpf
 * @since 3.1.0
 */
public class Spectator<T> implements Observer<T>, Serializable {

    private static final long serialVersionUID = 6492840418858364272L;

    public static int retry_times;

    private final Supplier<T> run;

    private final Class<T> type;

    private final Predicate<T> check;

    private final Function<T, String> simpleMsgHandler;

    private Class<? extends Throwable>[] specialRetry;

    private long exceptionRetryRestTime;

    private Flowable<T> flowable;

    public static final String retry_times_sign = "rxjava3.retry.times";

    static {
        //By default, it is taken from the system configuration If not, defaults to 2
        retry_times = SystemUtils.getPropertyWithConvert(retry_times_sign, Integer::parseInt, 2);
        //Preload SpectatorUtils
        SpectatorUtils.preload();
    }

    /**
     * On the Construction Method of Important Necessary Parameters
     *
     * @param run              Run Method Provider
     * @param type             Success condition assertion
     * @param check            Operational condition assertion
     * @param simpleMsgHandler Exception occurrence collection function
     */
    public Spectator(Supplier<T> run,
                     Class<T> type,
                     Predicate<T> check,
                     Function<T, String> simpleMsgHandler) {
        AssertUtils.Operation.notNull(run, "run no be null");
        AssertUtils.Operation.notNull(type, "type no be null");
        AssertUtils.Operation.notNull(check, "check no be null");
        AssertUtils.Operation.notNull(simpleMsgHandler, "simpleMsgHandler no be null");
        this.run = run;
        this.type = type;
        this.check = check;
        this.simpleMsgHandler = simpleMsgHandler;
    }

    /**
     * Provide static methods for constructing important necessary parameters
     *
     * @param run              Run Method Provider
     * @param type             Success condition assertion
     * @param check            Operational condition assertion
     * @param simpleMsgHandler Exception occurrence collection function
     * @param <T>              Parameter Generics
     * @return return parameters
     */
    public static <T> Spectator<T> prepare(Supplier<T> run,
                                           Class<T> type,
                                           Predicate<T> check,
                                           Function<T, String> simpleMsgHandler) {
        return new Spectator<>(run, type, check, simpleMsgHandler);
    }

    /**
     * Interval rest time setting for abnormal retries
     *
     * @param exceptionRetryRestTime Enrichment call gap with exception
     * @return {@literal Spectator of return parameters}
     */
    public Spectator<T> exceptionRetryTime(Long exceptionRetryRestTime) {
        this.exceptionRetryRestTime = exceptionRetryRestTime;
        return this;
    }

    /**
     * Special retry exception class object set setting
     *
     * @param specialRetry Retrying the specified exception group
     * @return {@literal Spectator of return parameters}
     */
    public Spectator<T> specialRetry(Class<? extends Throwable>[] specialRetry) {
        this.specialRetry = specialRetry;
        return this;
    }

    /**
     * Conduct a method inspection run based on important necessary parameters
     *
     * @return {@literal Spectator of return parameters}
     */
    public Spectator<T> run() {
        this.flowable = run(run, type, check, simpleMsgHandler);
        return this;
    }

    /**
     * Arrange functions for normal and abnormal consumers
     *
     * @param consumer   Normal consumers
     * @param exConsumer Abnormal consumers
     */
    public void accept(Consumer<T> consumer, Consumer<? super Throwable> exConsumer) {
        Disposable subscribe = this.flowable.subscribe(consumer, exConsumer);
        //add Disposable Scheduled to clear
        SpectatorUtils.addDisposable(subscribe);
    }

    @Override
    @NotNull
    public BackpressureStrategy strategy() {
        return BackpressureStrategy.LATEST;
    }

    @Override
    public int getRetryTimes() {
        return retry_times;
    }

    @Override
    public long exceptionRetryRestTime() {
        return exceptionRetryRestTime;
    }

    @Override
    public Class<? extends Throwable>[] specialRetry() {
        return specialRetry;
    }
}
