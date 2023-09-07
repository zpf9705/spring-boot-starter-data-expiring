package io.github.zpf9705.expiring.spring_jdk.example_cron;

import cn.hutool.cron.listener.TaskListener;

/**
 * The monitoring interface for timed task start, end, and exception mainly relies on {@link TaskListener},
 * which can be registered through {@link CronRegister#addListener(CronListener...)} and used for task
 * monitoring during startup.
 *
 * @author zpf
 * @since 3.2.2
 */
public interface CronListener extends TaskListener {
}
