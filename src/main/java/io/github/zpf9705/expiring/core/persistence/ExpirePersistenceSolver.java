package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.ExpireFactoryNameHolder;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
    public void putPersistence(@NonNull K key, @NonNull V value,
                               @Nullable Long duration,
                               @Nullable TimeUnit timeUnit) {
        //get current thread factory name
        String factoryName = ExpireFactoryNameHolder.getFactoryName();
        //async
        run(() -> {
            AssertUtils.Persistence.hasText(factoryName, "factoryName no be null");
            @SuppressWarnings("unchecked")
            ExpireSimpleGlobePersistence<K, V> put =
                    ExpireSimpleGlobePersistence.ofSet(
                            ExpireSimpleGlobePersistence.class,
                            ExpireSimpleGlobePersistence.Persistence.class,
                            Entry.of(key, value, duration, timeUnit), factoryName);
            //判断是否已经写入
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "putPersistence");
        //after reset current thread Expire Factory Name
        ExpireFactoryNameHolder.restFactoryNamedContent();
    }

    @Override
    public void replaceValuePersistence(@NonNull K key, @NonNull V value, @NonNull V newValue) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replace = ExpireSimpleGlobePersistence.ofGet(key, value, null);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    @Override
    public void replaceDurationPersistence(@NonNull K key, @NonNull V value,
                                           @NonNull Long duration, @NonNull TimeUnit timeUnit) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> replaceDuration = ExpireSimpleGlobePersistence.ofGet(key, value, null);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    @Override
    public void restDurationPersistence(@NonNull K key, @NonNull V value) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> reset = ExpireSimpleGlobePersistence.ofGet(key, value, null);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    @Override
    public void removePersistence(@NonNull K key, @NonNull V value) {
        run(() -> {
            ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key, value, null);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    @Override
    public void removePersistenceWithKey(@NonNull K key) {
        run(() -> {
                ExpireSimpleGlobePersistence<K, V> remove = ExpireSimpleGlobePersistence.ofGet(key);
                if (!remove.persistenceExist()) {
                    return;
                }
                remove.removePersistence();
        }, "removePersistenceWithKey");
    }

    @Override
    public void removeSimilarKeyPersistence(@NonNull K key) {
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
