package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.banner.StartUpBanner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.Banner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.io.PrintStream;

/**
 * Here is the client configuration of abstract methods {@code Banner} {@code Environment}
 * Life cycle in the client configuration, print the client's banner and recovery behavior after restart,
 * can expand the other methods
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbstractExpireConfiguration implements InitializingBean, Banner, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() {
        //print client banner
        printBanner(this.environment, getSourceClass(), System.out);
    }

    public abstract void printBanner(Environment environment, Class<?> sourceClass, PrintStream out);

    /**
     * Get banner source class
     *
     * @return a using banner class type no be {@literal null}
     */
    @NonNull
    public abstract Class<?> getSourceClass();

    /**
     * Using banner start up banner
     *
     * @return a {@link StartUpBanner} no be {@literal null}
     */
    @NonNull
    public abstract StartUpBanner getStartUpBanner();

    /**
     * Persistence action for other client
     *
     * @param path a persistence path
     * @return persistence sync result
     */
    public String persistenceRegain(String path) {
        return "OK";
    }
}
