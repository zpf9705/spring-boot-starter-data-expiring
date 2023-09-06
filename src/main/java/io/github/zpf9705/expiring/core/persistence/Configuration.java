package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.SystemUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Persistent files related configuration
 *
 * @author zpf
 * @since 3.0.0
 */
public final class Configuration {

    public static final String open_persistence = "csp.expiry.openPersistence";
    public static final String persistenceRunAsync = "csp.expiry.persistenceRunAsync";
    public static final String persistence_path = "csp.expiry.persistencePath";
    public static final String noPersistenceOfExpireTime = "csp.expiry.noPersistenceOfExpireTime";
    public static final String noPersistenceOfExpireTimeUnit = "csp.expiry.noPersistenceOfExpireTimeUnit";
    public static final String defaultExpireTime = "csp.expiry.defaultExpireTime";
    public static final String defaultExpireTimeUnit = "csp.expiry.defaultExpireTimeUnit";
    public static final String chooseClient = "csp.expiry.chooseClient";
    public static final String listeningRecoverySubPath = "csp.expiry.listening.recovery.path";
    static final long defaultNoPersistenceExpireTimeExample = 10L;
    static final long defaultExpireTimeExample = 20L;
    static boolean defaultCompareWithExpirePersistence = false;
    static AtomicBoolean load = new AtomicBoolean(false);
    static long defaultNoPersistenceExpireTimeToMille;
    private static final Configuration CONFIGURATION = new Configuration();

    private Configuration() {
    }

    public static Configuration getConfiguration() {
        return CONFIGURATION;
    }

    static {
        compareDefaultCompareWithExpirePersistence();
    }

    /**
     * Comparison when no input expiration time, the default Settings or default default
     * expiration and default not persistent value of the size of the timestamp
     */
    public static void compareDefaultCompareWithExpirePersistence() {
        if (!load.compareAndSet(false, true)) {
            return;
        }
        Configuration configuration = getConfiguration();
        TimeUnit defaultExpireTimeUnit = configuration.getDefaultExpireTimeUnit();
        if (defaultExpireTimeUnit == null) {
            return;
        }
        TimeUnit noPersistenceOfExpireTimeUnit = configuration.getNoPersistenceOfExpireTimeUnit();
        if (noPersistenceOfExpireTimeUnit == null) {
            return;
        }
        defaultNoPersistenceExpireTimeToMille =
                noPersistenceOfExpireTimeUnit.toMillis(configuration.getNoPersistenceOfExpireTime());
        try {
            defaultCompareWithExpirePersistence = defaultExpireTimeUnit.toMillis(configuration.getDefaultExpireTime())
                    >= defaultNoPersistenceExpireTimeToMille;
        } catch (Exception ignored) {
        }
    }

    /**
     * For whether to open the cache persistent system configuration button
     *
     * @return if {@code true} will open
     */
    public boolean getOpenPersistence() {
        return SystemUtils.getPropertyWithConvert(open_persistence, Boolean::parseBoolean, false);
    }

    /**
     * For whether to open the cache persistence operation asynchronous execution system configuration
     *
     * @return if {@code true} will Async
     */
    public boolean getPersistenceAsync() {
        return SystemUtils.getPropertyWithConvert(persistenceRunAsync, Boolean::parseBoolean, false);
    }

    /**
     * The persistent cache disk write path system configuration
     *
     * @return if not null {@code PersistencePath} will write this path
     */
    public String getPersistencePath() {
        return SystemUtils.getPropertyWithConvert(persistence_path, Function.identity(), null);
    }

    /**
     * Obtain a maximum time of system configuration without persistence
     *
     * @return Below this time will not persistent
     */
    public long getNoPersistenceOfExpireTime() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTime, Long::parseLong,
                defaultNoPersistenceExpireTimeExample);
    }

    /**
     * Obtain a maximum time unit of system configuration without persistence
     *
     * @return {@link #getNoPersistenceOfExpireTime()}
     */
    public TimeUnit getNoPersistenceOfExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(noPersistenceOfExpireTimeUnit, TimeUnit::valueOf, null);
    }

    /**
     * Get the default cache time system configuration
     *
     * @return If you don't set the cache time, will use the configured
     */
    public long getDefaultExpireTime() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTime, Long::parseLong, defaultExpireTimeExample);
    }

    /**
     * Get the default cache time unit system configuration
     *
     * @return {@link #getDefaultExpireTime()}
     */
    public TimeUnit getDefaultExpireTimeUnit() {
        return SystemUtils.getPropertyWithConvert(defaultExpireTimeUnit, TimeUnit::valueOf, null);
    }

    /**
     * Get choose cache client
     *
     * @return choose client
     */
    public String getChooseClient() {
        return SystemUtils.getPropertyWithConvert(chooseClient, Function.identity(), null);
    }

    /**
     * Get listening recovery path with finding sub for {@link ListeningRecovery}
     *
     * @return choose client
     */
    public String[] getListeningRecoverySubPath() {
        return SystemUtils.getPropertyWithConvert(listeningRecoverySubPath, ArrayUtils::toStringArrayToConvertArray,
                null);
    }

    /**
     * Whether you need to obtain the default persistent identity
     *
     * @return if {@code true} persistence right
     */
    public boolean isDefaultCompareWithExpirePersistence() {
        return defaultCompareWithExpirePersistence;
    }

    /**
     * Get the default not persistent {@link TimeUnit#toMillis(long)} value
     *
     * @return result
     */
    public long getDefaultNoPersistenceExpireTimeToMille() {
        return defaultNoPersistenceExpireTimeToMille;
    }
}
