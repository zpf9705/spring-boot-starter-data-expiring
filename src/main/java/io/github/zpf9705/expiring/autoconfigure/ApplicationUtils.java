package io.github.zpf9705.expiring.autoconfigure;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.UtilsException;
import org.springframework.boot.SpringApplication;

import java.util.Objects;
import java.util.Set;

/**
 * {@link SourceEnvironmentPostProcessor} to find Springboot primarySources.
 *
 * @author zpf
 * @since 3.1.5
 */
public final class ApplicationUtils {

    private ApplicationUtils() {
    }

    private static String[] defaultPackage;

    /**
     * Take the package path information where the springboot main class is located.
     *
     * @return According to {@code  org.springframework.boot.SpringApplication#primarySources},
     * there can be multiple main class paths and must not be {@literal  null}.
     */
    public static String[] findSpringApplicationPackageName() {
        return defaultPackage;
    }

    /**
     * Obtain the spring boot startup main class information in
     * {@link io.github.zpf9705.expiring.autoconfigure.SourceEnvironmentPostProcessor}.
     * Before initializing the spring boot container, please refer to
     * {@link io.github.zpf9705.expiring.autoconfigure.SourceEnvironmentPostProcessor} and learn about
     * {@link org.springframework.boot.env.EnvironmentPostProcessor}
     *
     * @param source {@link SpringApplication#getAllSources()}
     */
    public static void applicationSource(@NotNull Set<Object> source) {
        if (source.isEmpty()) {
            throw new UtilsException("No detection of the existence of the startup main class");
        }
        defaultPackage = source.stream().map(o -> {
            if (o instanceof Class<?>) {
                return ((Class<?>) o).getName().split(
                        "\\." + ((Class<?>) o).getSimpleName())[0];
            }
            return null;
        }).filter(Objects::nonNull).toArray(String[]::new);
    }
}
