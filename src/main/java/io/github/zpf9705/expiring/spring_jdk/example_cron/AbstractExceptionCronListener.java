package io.github.zpf9705.expiring.spring_jdk.example_cron;

import cn.hutool.cron.TaskExecutor;

/**
 * For abstract classes implemented by exception methods, the signals for startup and success can be selectively rewritten.
 *
 * @author zpf
 * @since 3.5.2
 */
public abstract class AbstractExceptionCronListener implements CronListener {

    @Override
    public void onStart(TaskExecutor executor) {

    }

    @Override
    public void onSucceeded(TaskExecutor executor) {

    }

    @Override
    public abstract void onFailed(TaskExecutor executor, Throwable exception);
}
