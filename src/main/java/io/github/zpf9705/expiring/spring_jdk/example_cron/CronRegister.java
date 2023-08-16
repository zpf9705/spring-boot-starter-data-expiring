package io.github.zpf9705.expiring.spring_jdk.example_cron;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.cron.CronUtil;
import io.github.zpf9705.expiring.spring_jdk.example_cron.annotation.Cron;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Timed task registrar, which relies on domestic Hutu {@link cn.hutool.Hutool} toolkit for implementation. Thank you very much
 *
 * @author zpf
 * @since 3.1.5
 */
class CronRegister {

    public static void register(Object targetObj, Method method) {
        if (targetObj == null || method == null) {
            return;
        }
        Cron cron = method.getAnnotation(Cron.class);
        //Register scheduled tasks
        CronUtil.schedule(cron.express(), (Runnable) () -> ReflectUtil.invoke(targetObj, method,
                (Object) cron.args()));
    }

    public static void start(boolean isMatchSecond, boolean isDaemon) {
        //Second matching support
        CronUtil.setMatchSecond(isMatchSecond);
        //Configurable to set up daemon threads
        CronUtil.start(isDaemon);
    }

    public static Set<Method> getScanMethodsWithAnnotation(String... scanPackage) {
        // get any class finder
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().forPackages(scanPackage)
        );
        //load any method
        return reflections.getMethodsAnnotatedWith(Cron.class);
    }
}
