//
// Copyright: Hangzhou Boku Network Co., Ltd
// ......
// Copyright Maintenance Date: 2022-2023
// ......
// Direct author: Zhang Pengfei
// ......
// Author email: 929160069@qq.com
// ......
// Please indicate the source for reprinting use
//

package io.github.zpf9705.expiring.util.rxjava;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author zpf
 * @since 1.0.0 - [2023-08-01 10:31]
 */
public class CompletableSpectator<T> extends Spectator<T> implements Observer<T> {

    private static final long serialVersionUID = 1262405762807640190L;

    /**
     * On the Construction Method of Important Necessary Parameters
     *
     * @param run              Run Method Provider
     * @param type             Success condition assertion
     * @param check            Operational condition assertion
     * @param simpleMsgHandler Exception occurrence collection function
     */
    public CompletableSpectator(Supplier<T> run,
                                Class<T> type,
                                Predicate<T> check,
                                Function<T, String> simpleMsgHandler) {
        super(run, type, check, simpleMsgHandler);
    }

    @Override
    @NotNull
    public BackpressureStrategy strategy() {
        return BackpressureStrategy.ERROR;
    }

    @Override
    public Executor getExecutor() {
        return ForkJoinPool.commonPool();
    }
}
