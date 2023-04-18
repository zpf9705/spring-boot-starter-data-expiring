package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Persistent cache files to restore factory abstract class, through the collection of {@link ServiceLoader}
 * in the factory,according to the different implementations for recovery plant selection
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class ExpireGlobePersistenceRenewFactory {

    /**
     * The query cache persistent chemical plant
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    public static PersistenceRenewFactory getPersistenceFRenewFactory(Class<?> factoryClass) {
        if (factoryClass == null) {
            return null;
        }
        return getPersistenceFRenewFactoryWithClass(factoryClass);
    }

    /**
     * The query cache persistent chemical plant with client class type
     *
     * @param factoryClass factory class
     * @return implements {@code PersistenceFactory}
     */
    private static PersistenceRenewFactory getPersistenceFRenewFactoryWithClass(@NotNull Class<?> factoryClass) {
        PersistenceRenewFactory factory = PersistenceFactoryProvider.findRenewFactory(factoryClass);
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
        private static List<PersistenceRenewFactory> PERSISTENCE_FACTORIES;

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
            Iterator<PersistenceRenewFactory> factoryIterator =
                    ServiceLoadUtils.load(PersistenceRenewFactory.class).loadAllSubInstance();
            if (factoryIterator != null)
                factoryIterator.forEachRemaining(factory -> PERSISTENCE_FACTORIES.add(factory));
        }

        /**
         * According to the class from the cache object to obtain the corresponding factory
         *
         * @param factoryClass factory class
         * @return implements {@code PersistenceFactory}
         */
        public static PersistenceRenewFactory findRenewFactory(@NotNull Class<?> factoryClass) {
            return PERSISTENCE_FACTORIES.stream()
                    .filter(f -> Objects.equals(factoryClass.getName(), f.getFactoryName()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
