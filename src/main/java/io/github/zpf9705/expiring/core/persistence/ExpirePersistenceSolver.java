package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Simple Cache persistence for {@link PersistenceSolver}
 *
 * @author zpf
 * @since 1.1.0
 */
public class ExpirePersistenceSolver<K, V> implements PersistenceSolver<K, V> {

    @Override
    public void putPersistence(@NotNull K key, @NotNull V value,
                               @CanNull Long duration,
                               @CanNull TimeUnit timeUnit) {
        run(() -> {
            @SuppressWarnings("unchecked")
            ExpireSimpleGlobePersistence<K, V> put =
                    ExpireSimpleGlobePersistence.ofSet(
                            ExpireSimpleGlobePersistence.class,
                            ExpireSimpleGlobePersistence.Persistence.class,
                            Entry.of(key, value, duration, timeUnit));
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "putPersistence");
    }

    @Override
    public void replaceValuePersistence(@NotNull K key, @NotNull V newValue) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replace = ExpireSimpleGlobePersistence.ofGet(key);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    @Override
    public void replaceDurationPersistence(@NotNull K key, @NotNull Long duration, @NotNull TimeUnit timeUnit) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replaceDuration = ExpireSimpleGlobePersistence.ofGet(key);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    @Override
    public void restDurationPersistence(@NotNull K key) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> reset = ExpireSimpleGlobePersistence.ofGet(key);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    @Override
    public void removePersistence(@NotNull K key, @NotNull V value) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key, value, null);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    @Override
    public void removePersistenceWithKey(@NotNull K key) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key);
            if (!remove.persistenceExist()) {
                return;
            }
            remove.removePersistence();
        }, "removePersistenceWithKey");
    }

    @Override
    public void removeSimilarKeyPersistence(@NotNull K key) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            List<ExpireSimpleGlobePersistence<K, V>> similar = ExpireSimpleGlobePersistence.ofGetSimilar(key);
            AssertUtils.Persistence.notEmpty(similar, "No found key [" + key + "] similar persistence");
            similar.forEach(s -> {
                if (s.persistenceExist()) {
                    s.removePersistence();
                }
            });
        }, "removePersistence");
    }

    @Override
    public void removeAllPersistence() {
        delAll();
    }
}
