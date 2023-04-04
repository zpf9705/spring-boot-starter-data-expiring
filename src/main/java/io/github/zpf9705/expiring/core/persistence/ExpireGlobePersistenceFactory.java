package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.util.AssertUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Expire Globe Persistence Factory for each client singleton
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireGlobePersistenceFactory {

    public static PersistenceFactory getPersistenceFactory(String clientName) {
        if (StringUtils.isBlank(clientName)) {
            return null;
        }
        return getPersistenceFactoryWithClientName(clientName);
    }

    private static PersistenceFactory getPersistenceFactoryWithClientName(String clientName) {
        PersistenceFactory factory = PersistenceFactoryEnum.findByClientName(clientName);
        AssertUtils.Persistence.notNull(factory, "Sorry , no found clint name [" + clientName + "] " +
                "Persistence Factory");
        return factory;
    }

    enum PersistenceFactoryEnum {

        SIMPLE(ExpireSimpleGlobePersistence.INSTANCE);

        final PersistenceFactory factory;

        PersistenceFactoryEnum(PersistenceFactory factory) {
            this.factory = factory;
        }

        public PersistenceFactory getFactory() {
            return factory;
        }

        private static final List<PersistenceFactory> PERSISTENCE_FACTORIES =
                Arrays.stream(PersistenceFactoryEnum.values()).map(PersistenceFactoryEnum::getFactory)
                        .collect(Collectors.toList());

        public static PersistenceFactory findByClientName(String clientName) {
            return PERSISTENCE_FACTORIES.stream()
                    .filter(f -> Objects.equals(clientName, f.getFactoryName()))
                    .findFirst()
                    .orElse(null);
        }
    }
}
