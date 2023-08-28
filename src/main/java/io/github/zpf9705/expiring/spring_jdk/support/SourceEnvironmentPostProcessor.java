package io.github.zpf9705.expiring.spring_jdk.support;

import io.github.zpf9705.expiring.util.ScanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * The implementation of information collection before the creation of the spring container here,
 * including the package path where the main class is started, plays a crucial role in the scalability of
 * the example package in the future.
 *
 * @author zpf
 * @since 3.2.0
 */
public class SourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ScanUtils.applicationSource(application.getAllSources());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
