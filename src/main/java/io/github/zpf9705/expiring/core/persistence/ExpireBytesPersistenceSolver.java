package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.ExpireFactoryNameHolder;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Bytes Cache persistence for {@link PersistenceSolver}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireBytesPersistenceSolver implements PersistenceSolver<byte[], byte[]> {

    @Override
    public void putPersistence(@NonNull byte[] key, @NonNull byte[] value,
                               @Nullable Long duration,
                               @Nullable TimeUnit timeUnit) {
        //get current thread factory name
        String factoryName = ExpireFactoryNameHolder.getFactoryName();
        //async
        run(() -> {
            AssertUtils.Persistence.hasText(factoryName, "factoryName no be null");
            ExpireByteGlobePersistence put =
                    ExpireByteGlobePersistence
                            .ofSetBytes(Entry.of(key, value, duration, timeUnit), factoryName);
            //判断是否已经写入
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "putPersistence");
        //after reset current thread Expire Factory Name
        ExpireFactoryNameHolder.restFactoryNamedContent();
    }

    @Override
    public void replaceValuePersistence(@NonNull byte[] key, @NonNull byte[] value,
                                        @NonNull byte[] newValue) {
        run(() -> {
            ExpireByteGlobePersistence replace = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "replacePersistence");
    }

    @Override
    public void replaceDurationPersistence(@NonNull byte[] key, @NonNull byte[] value,
                                           @NonNull Long duration, @NonNull TimeUnit timeUnit) {
        run(() -> {
            ExpireByteGlobePersistence replaceDuration = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "setEPersistence");
    }

    @Override
    public void restDurationPersistence(@NonNull byte[] key, @NonNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence reset = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "restPersistence");
    }

    @Override
    public void removePersistence(@NonNull byte[] key, @NonNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist");
            remove.removePersistence();
        }, "removePersistence");
    }

    @Override
    public void removePersistenceWithKeys(@NonNull byte[]... keys) {
        run(() -> {
            for (byte[] key : keys) {
                ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key);
                if (!remove.persistenceExist()) {
                    continue;
                }
                remove.removePersistence();
            }
        }, "removePersistenceWithKeys");
    }

    @Override
    public void removeSimilarKeyPersistence(@NonNull byte[] key) {
        run(() -> {
            AssertUtils.Persistence.notNull(key, "key no be null");
            List<ExpireByteGlobePersistence> similar = ExpireByteGlobePersistence.ofGetSimilarBytes(key);
            AssertUtils.Persistence.notEmpty(similar,
                    "No found key [" + Arrays.toString(key) + "] similar persistence");
            similar.forEach(s -> {
                if (s.persistenceExist()) {
                    s.removePersistence();
                }
            });
        }, "removePersistence");
    }

    @Override
    public void removeAllPersistence() {
        run(ExpireSimpleGlobePersistence.INSTANCE::removeAllPersistence, "removeAllPersistence");
    }
}
