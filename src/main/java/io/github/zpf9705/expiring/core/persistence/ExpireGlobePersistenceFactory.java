package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Expire Globe Persistence Factory for each client singleton
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ExpireGlobePersistenceFactory {

    /**
     * The query cache persistent chemical plant
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    public static PersistenceFactory getPersistenceFactory(Class<?> factoryClass) {
        if (factoryClass == null) {
            return null;
        }
        return getPersistenceFactoryWithClassName(factoryClass);
    }

    /**
     * The query cache persistent chemical plant with client class name
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    private static PersistenceFactory getPersistenceFactoryWithClassName(@NonNull Class<?> factoryClass) {
        PersistenceFactory factory = PersistenceFactoryProvider.findByClient(factoryClass);
        AssertUtils.Persistence.notNull(factory, "Sorry , no found clint name [" + factoryClass.getName() + "] " +
                "Persistence Factory");
        return factory;
    }

    /**
     * The query cache persistent chemical plant provider
     */
    static class PersistenceFactoryProvider {

        /**
         * cache factory client
         */
        private static List<PersistenceFactory> PERSISTENCE_FACTORIES;

        /**
         * initialize sign
         */
        private static final AtomicBoolean initialize = new AtomicBoolean(false);

        static {
            if (initialize.compareAndSet(false, true)) {
                PERSISTENCE_FACTORIES = new ArrayList<>();
            }
            /*
             * @see ServiceLoadUtils
             */
            Iterator<PersistenceFactory> factoryIterator =
                    ServiceLoadUtils.load(PersistenceFactory.class).loadAllSubInstance();
            factoryIterator.forEachRemaining(factory -> PERSISTENCE_FACTORIES.add(factory));
        }

        /**
         * According to the class from the cache object to obtain the corresponding factory
         *
         * @param factoryClass factory class
         * @return implements {@code PersistenceFactory}
         */
        public static PersistenceFactory findByClient(@NonNull Class<?> factoryClass) {
            return PERSISTENCE_FACTORIES.stream()
                    .filter(f -> Objects.equals(factoryClass.getName(), f.getFactoryName()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
