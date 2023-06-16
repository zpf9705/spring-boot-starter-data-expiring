package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Objects;

/**
 * This interface is the core interface for implementing caching methods
 * and subsequent cache recovery and deletion of persistent files
 * Inherited the help center (i.e. cache operation entity) {@link HelpCenter}
 * and recovery center (i.e. implementation of subsequent cache recovery methods) {@link ReloadCenter}
 * Distinguish the currently active cache clients based on a annotation {@link Client}
 *
 * @author zpf
 * @since 3.0.5
 */
public interface Center<C, K, V> extends HelpCenter<C>, ReloadCenter<K, V>, Serializable {

    /**
     * Indicate the currently activated client
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Client {
        String value();
    }

    /**
     * Obtain the center of the currently activated client based on the registered SPI interface
     *
     * @param clientName choose client name
     * @return {@link Center}
     */
    @SuppressWarnings("rawtypes")
    static Center getCenter(@NotNull String clientName) {
        Map<Class<?>, Center> CenterSpiMap = ServiceLoadUtils.load(Center.class).withClassMap();
        if (CollectionUtils.simpleNotEmpty(CenterSpiMap)) {
            for (Class<?> clazz : CenterSpiMap.keySet()) {
                //is interface pass this must find real implements
                if (clazz.isInterface()) {
                    continue;
                }
                Client client = clazz.getAnnotation(Client.class);
                if (client != null) {
                    if (Objects.equals(client.value(), clientName)) {
                        return CenterSpiMap.get(clazz);
                    }
                }
            }
        }
        throw new IllegalArgumentException("[ " + clientName + "] no found Center");
    }
}
