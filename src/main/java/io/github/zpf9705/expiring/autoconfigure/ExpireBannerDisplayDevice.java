package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.banner.StartUpBanner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.lang.NonNull;

import java.io.PrintStream;

/**
 * Banner display interface when the project is started
 * If you rewrite the {@link ExpireBannerDisplayDevice#afterPropertiesSet()}
 * Will need to call {@link ExpireBannerDisplayDevice#printBanner(Environment, Class, PrintStream)} banner information display
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireBannerDisplayDevice extends InitializingBean, Banner, EnvironmentCapable {

    @Override
    default void afterPropertiesSet() {
        //print client banner
        printBanner(getEnvironment(), getSourceClass(), System.out);
    }

    void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);

    @Override
    @NonNull
    Environment getEnvironment();

    /**
     * Get banner source class
     *
     * @return a using banner class type no be {@literal null}
     */
    @NonNull
    Class<?> getSourceClass();

    /**
     * Using banner start up banner
     *
     * @return a {@link StartUpBanner} no be {@literal null}
     */
    @NonNull
    StartUpBanner getStartUpBanner();
}
