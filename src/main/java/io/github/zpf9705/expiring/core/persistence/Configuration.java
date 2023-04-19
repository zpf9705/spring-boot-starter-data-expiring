package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.util.SystemUtils;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Persistent files related configuration
 *
 * @author zpf
 * @since 3.0.0
 */
public final class Configuration {

    public static final String open_persistence = "csp.expiry.openPersistence";
    public static final String persistence_path = "csp.expiry.persistencePath";
    public static final String noPersistenceOfExpireTime = "csp.expiry.noPersistenceOfExpireTime";
    public static final String noPersistenceOfExpireTimeUnit = "csp.expiry.noPersistenceOfExpireTimeUnit";
    public static final String defaultExpireTime = "csp.expiry.defaultExpireTime";
    public static final String defaultExpireTimeUnit = "csp.expiry.defaultExpireTimeUnit";
    static final long defaultNoPersistenceExpireTimeExample = 10L;
    static final long defaultExpireTimeExample = 20L;

    private static final Configuration CONFIGURATION = new Configuration();

    private Configuration() {
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    public boolean getOpenPersistence() {
        return SystemUtils.getPropertyWithConvert(open_persistence, Boolean::parseBoolean, false);
    }

    public String getPersistencePath() {
        return SystemUtils.getPropertyWithConvert(persistence_path, Function.identity(), null);
    }

    public long getNoPersistenceOfExpireTime() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTime, Long::parseLong,
                defaultNoPersistenceExpireTimeExample);
    }

    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTimeUnit, TimeUnit::valueOf, null);
    }

    public long getDefaultExpireTime() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTime, Long::parseLong, defaultExpireTimeExample);
    }

    public TimeUnit getDefaultExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTimeUnit, TimeUnit::valueOf, null);
    }
}
