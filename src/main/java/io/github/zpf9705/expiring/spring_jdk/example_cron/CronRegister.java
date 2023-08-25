package io.github.zpf9705.expiring.spring_jdk.example_cron;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.cron.CronUtil;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.spring_jdk.example_cron.annotation.Cron;
import io.github.zpf9705.expiring.spring_jdk.support.SupportException;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.ReflectionUtils;
import io.github.zpf9705.expiring.util.UtilsException;
import org.springframework.scheduling.support.CronExpression;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Timed task registrar, which relies on domestic Hutu {@link cn.hutool.Hutool} toolkit for implementation. Thank you very much
 *
 * @author zpf
 * @since 3.1.5
 */
class CronRegister {

    static final String second_match_key = "cron.match.second=";

    static final String thread_daemon_key = "cron.thread.daemon=";

    public static void register(Object targetObj, Method method) {
        if (targetObj == null || method == null) {
            return;
        }
        Cron cron = method.getAnnotation(Cron.class);
        if (!CronExpression.isValidExpression(cron.express())) {
            throw new UtilsException("Provider " + cron.express() + "no a valid cron express");
        }
        //Register scheduled tasks
        CronUtil.schedule(cron.express(), (Runnable) () -> ReflectUtil.invoke(targetObj, method,
                (Object) cron.args()));
    }

    public static List<Method> getScanMethodsWithCron(String... scanPackage) {
        if (ArrayUtils.simpleIsEmpty(scanPackage)) {
            return Collections.emptyList();
        }
        return ReflectionUtils.findPackagesOfMethodWithAnnotation(Cron.class, scanPackage);
    }

    /**
     * Notify the system of the startup parameters passed in by the main method to
     * determine the second configuration {@link #second_match_key} for scheduled task startup
     * <pre>
     *     {@code --cron.match.second=true}
     * </pre>
     * <p>
     * and
     * <p>
     * the configuration of the daemon thread {@link #thread_daemon_key}
     * <pre>
     *     {@code --cron.thread.daemon=false}
     * </pre>
     * Default to support second matching and non daemon threads
     *
     * @param args Starting command passing in parameters
     */
    public static void start(String... args) {
        if (CronUtil.getScheduler().isStarted()) {
            throw new SupportException("The start switch of cn.hutool.cron.CronUtil has been turned on before," +
                    " please check");
        }
        if (ArrayUtils.simpleIsEmpty(args)) {
            //empty args direct to matchSecond and thread daemon
            start(true, false);
        } else {
            int config = 0;
            String secondMatch = "true";
            String daemon = "false";
            for (String arg : args) {
                if (config == 2) {
                    break;
                }
                if (arg.contains(second_match_key)) {
                    secondMatch = arg.split(second_match_key)[1];
                    config++;
                } else if (arg.contains(thread_daemon_key)) {
                    daemon = arg.split(thread_daemon_key)[1];
                    config++;
                }
            }
            start(Boolean.parseBoolean(secondMatch), Boolean.parseBoolean(daemon));
        }
    }

    public static void start(boolean isMatchSecond, boolean isDaemon) {
        //Second matching support
        CronUtil.setMatchSecond(isMatchSecond);
        //Configurable to set up daemon threads
        CronUtil.start(isDaemon);
        //register info log
        Console.info("Cron register success : success num : {}", CronUtil.getScheduler().size());
    }
}
