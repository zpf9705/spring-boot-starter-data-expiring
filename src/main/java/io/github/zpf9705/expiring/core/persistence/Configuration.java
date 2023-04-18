package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.util.SystemUtils;
import java.util.concurrent.TimeUnit;

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

    private static final Configuration CONFIGURATION = new Configuration();

    private Configuration() {
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    public boolean getOpenPersistence() {
        String property = System.getProperty(open_persistence);
        if (property == null) {
            return false;
        }
        return Boolean.parseBoolean(property);
    }

    public String getPersistencePath() {
        String property = System.getProperty(persistence_path);
        if (property == null) {
            return SystemUtils.getCurrentProjectPath();
        }
        return property;
    }

    public Long getNoPersistenceOfExpireTime() {
        String property = System.getProperty(noPersistenceOfExpireTime);
        if (property == null) {
            return 10L;
        }
        return Long.parseLong(property);
    }

    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        String property = System.getProperty(noPersistenceOfExpireTimeUnit);
        if (property == null) {
            return TimeUnit.SECONDS;
        }
        return TimeUnit.valueOf(property);
    }

    public Long getDefaultExpireTime() {
        String property = System.getProperty(defaultExpireTime);
        if (property == null) {
            return 20L;
        }
        return Long.parseLong(property);
    }

    public TimeUnit getDefaultExpireTimeUnit() {
        String property = System.getProperty(defaultExpireTimeUnit);
        if (property == null) {
            return TimeUnit.SECONDS;
        }
        return TimeUnit.valueOf(property);
    }
}
