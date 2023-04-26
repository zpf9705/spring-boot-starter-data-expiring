package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.persistence.ExpireBytesPersistenceSolver;
import io.github.zpf9705.expiring.core.persistence.PersistenceSolver;
import io.github.zpf9705.expiring.help.HelpCenter;
import io.github.zpf9705.expiring.help.ReloadCarry;
import io.github.zpf9705.expiring.util.CollectionUtils;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Expire Map Configuration Center
 * According to the configuration to create a singleton {@code ExpiringMap}
 * At the same time providing a cache recovery heavy load
 * <dl>
 *     <dt>configuration</dt>
 *     <dt>Singleton</dt>
 *     <dt>reload</dt>
 * </dl>
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ExpireMapCenter implements HelpCenter<ExpireMapCenter>, ReloadCarry<byte[], byte[]> {

    /**
     * Singleton for {@link ExpireMapCenter}
     */
    private static volatile ExpireMapCenter expireMapCenter;

    private final ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton;

    private ExpireMapCenter(ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton) {
        this.solveDifferentialGenericSingleton = solveDifferentialGenericSingleton;
    }

    /**
     * Singleton with {@code ExpireMapClientConfiguration}
     *
     * @param configuration must no be {@literal null}
     * @return {@link net.jodah.expiringmap.ExpiringMap}
     */
    public static ExpireMapCenter singletonWithConfiguration(@NotNull ExpireMapClientConfiguration configuration) {
        if (expireMapCenter == null) {
            synchronized (ExpireMapCenter.class) {
                if (expireMapCenter == null) {
                    expireMapCenter = buildSingleton(configuration);
                }
            }
        }
        return expireMapCenter;
    }

    /**
     * Get Singleton instance for {@code ExpireMapCenter}
     *
     * @return {@link ExpireMapCenter}
     */
    public static ExpireMapCenter getExpireMapCenter() {
        if (expireMapCenter == null) {
            throw new OperationsException("ExpireMapCenter need Initialize");
        }
        return expireMapCenter;
    }

    /**
     * Get operation with a {@code ExpiringMap}
     *
     * @return {@link net.jodah.expiringmap.ExpiringMap}
     */
    public ExpiringMap<byte[], byte[]> getExpiringMap() {
        return this.solveDifferentialGenericSingleton;
    }

    @Override
    public ExpireMapCenter getHelpCenter() {
        return getExpireMapCenter();
    }

    /**
     * Reload maybe persistence or Other need to recover.
     *
     * @param key      must no be {@literal null}
     * @param value    must no be {@literal null}
     * @param duration must no be {@literal null}
     * @param unit     must no be {@literal null}
     */
    @Override
    public void reload(@NotNull byte[] key, @NotNull byte[] value, @NotNull Long duration,
                       @NotNull TimeUnit unit) {
        if (this.solveDifferentialGenericSingleton == null) {
            return;
        }
        this.solveDifferentialGenericSingleton.put(key, value, duration, unit);
        this.getContain().putIfAbsent(key, value);
    }

    /**
     * Build Singleton with {@code ExpireMapClientConfiguration}.
     *
     * @param configuration must no be {@literal null}
     * @return {@link net.jodah.expiringmap.ExpiringMap}
     */
    private static ExpireMapCenter buildSingleton(@NotNull ExpireMapClientConfiguration configuration) {
        ExpiringMap<byte[], byte[]> solveDifferentialGenericSingleton = ExpiringMap.builder()
                .maxSize(configuration.getMaxSize())
                .expiration(configuration.getDefaultExpireTime(), configuration.getDefaultExpireTimeUnit())
                .expirationPolicy(configuration.getExpirationPolicy())
                .variableExpiration()
                .build();
        if (CollectionUtils.simpleNotEmpty(configuration.getSyncExpirationListeners())) {
            for (ExpirationListener expirationListener : configuration.getSyncExpirationListeners()) {
                //sync
                solveDifferentialGenericSingleton.addExpirationListener(expirationListener);

            }
        }
        if (CollectionUtils.simpleNotEmpty(configuration.getASyncExpirationListeners())) {
            for (ExpirationListener expirationListener : configuration.getASyncExpirationListeners()) {
                //async
                solveDifferentialGenericSingleton.addAsyncExpirationListener(expirationListener);

            }
        }
        return new ExpireMapCenter(solveDifferentialGenericSingleton);
    }

    /**
     * Remove expired keys of auxiliary elements.
     * <dl>
     *     <dt>Contain</dt>
     *     <dt>Persistence</dt>
     * </dl>
     *
     * @param key   must not be {@literal null}
     * @param value must not be {@literal null}
     */
    public void cleanSupportingElements(@NotNull byte[] key, @NotNull byte[] value) {
        //Remove control information
        this.getContain().remove(key, value);
        //Remove persistent cache
        PersistenceSolver<byte[], byte[]> solver = ServiceLoadUtils.load(PersistenceSolver.class)
                .getSpecifiedServiceBySubClass(ExpireBytesPersistenceSolver.class);
        if (solver != null) {
            solver.removePersistence(key, value);
        }
    }
}
