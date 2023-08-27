package io.github.zpf9705.expiring.spring_jdk.support;

import io.github.zpf9705.expiring.util.ScanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
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
