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
 * It mainly includes the following aspects：
 * <table cellspacing=8 cellpadding=0 ">
 *     <tr style="background-color: rgb(204, 204, 255);">
 *         <th align=left>run
 *         <th align=left>
 *         <th align=left>
 *         <th align=left>type
 *         <th align=left>
 *         <th align=left>
 *         <th align=left>check
 *         <th align=left>
 *         <th align=left>
 *         <th align=left>simpleMsgHandler
 *     </tr>
 *          <td>[Method operation provided]</td>
 *          <td></td>
 *          <td></td>
 *          <td>[Return value conversion type]</td>
 *          <td></td>
 *          <td></td>
 *          <td>[Callback interface check assertion]</td>
 *          <td></td>
 *          <td></td>
 *          <td>[Return information formatting function]</td>
 * </table>
 * <p>
 * The specific application method of the above parameters can be queried
 * {@link Observer#run(Supplier, Class, Predicate, Function)}
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

    private Flowable<T> flowable;

    static {
        retry_times = SystemUtils.getPropertyWithConvert("rxjava3.retry.times", Integer::parseInt, 2);
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
     * @return {@link Disposable} Subscription Results
     */
    public Disposable accept(Consumer<T> consumer, Consumer<? super Throwable> exConsumer) {
        return this.flowable.subscribe(consumer, exConsumer);
    }

    @Override
    @NotNull
    public BackpressureStrategy selectBack() {
        return BackpressureStrategy.LATEST;
    }

    @Override
    public int getRetryTimes() {
        return retry_times;
    }
}
