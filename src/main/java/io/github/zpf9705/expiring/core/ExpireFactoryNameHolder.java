package io.github.zpf9705.expiring.core;

import org.springframework.core.NamedThreadLocal;

/**
 * Save current thread operation {@link ExpireTemplate} factory name
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireFactoryNameHolder {

    private static final NamedThreadLocal<String> factoryNamedContext =

            new NamedThreadLocal<>("CURRENT EXPIRE FACTORY NAME");

    /**
     * Save value of context
     *
     * @param factoryName set expire factoryName
     */
    public static void setFactoryName(String factoryName) {
        if (factoryName == null) {
            restFactoryNamedContent();
        } else {
            factoryNamedContext.set(factoryName);
        }
    }

    /**
     * Rest value of context
     */
    public static void restFactoryNamedContent() {
        factoryNamedContext.remove();
    }

    /**
     * Get current thread factory name
     *
     * @return {@link ExpireTemplate#getFactoryBeanName()}
     */
    public static String getFactoryName() {
        return factoryNamedContext.get();
    }
}
