package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;
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
    public void putPersistence(@NotNull byte[] key, @NotNull byte[] value,
                               @CanNull Long duration,
                               @CanNull TimeUnit timeUnit) {

        run(() -> {
            ExpireByteGlobePersistence put =
                    ExpireByteGlobePersistence
                            .ofSetBytes(Entry.of(key, value, duration, timeUnit));
            AssertUtils.Persistence.isTrue(!put.persistenceExist(), "persistence already exist ");
            put.serial();
        }, "ExpireBytesPersistenceSolver::putPersistence");
    }

    @Override
    public void replaceValuePersistence(@NotNull byte[] key, @NotNull byte[] value,
                                        @NotNull byte[] newValue) {
        run(() -> {
            ExpireByteGlobePersistence replace = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(replace.persistenceExist(), "persistence no exist");
            replace.replacePersistence(newValue);
        }, "ExpireBytesPersistenceSolver::replacePersistence");
    }

    @Override
    public void replaceDurationPersistence(@NotNull byte[] key, @NotNull byte[] value,
                                           @NotNull Long duration, @NotNull TimeUnit timeUnit) {
        run(() -> {
            ExpireByteGlobePersistence replaceDuration = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(replaceDuration.persistenceExist(), "persistence no exist");
            replaceDuration.setExpirationPersistence(duration, timeUnit);
        }, "ExpireBytesPersistenceSolver::setEPersistence");
    }

    @Override
    public void restDurationPersistence(@NotNull byte[] key, @NotNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence reset = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(reset.persistenceExist(), "persistence no exist");
            reset.resetExpirationPersistence();
        }, "ExpireBytesPersistenceSolver::restPersistence");
    }

    @Override
    public void removePersistence(@NotNull byte[] key, @NotNull byte[] value) {
        run(() -> {
            ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key, value);
            AssertUtils.Persistence.isTrue(remove.persistenceExist(), "persistence no exist, no repeat del");
            remove.removePersistence();
        }, "ExpireBytesPersistenceSolver::removePersistence");
    }

    @Override
    public void removePersistenceWithKey(@NotNull byte[] key) {
        run(() -> {
            ExpireByteGlobePersistence remove = ExpireByteGlobePersistence.ofGetBytes(key);
            if (!remove.persistenceExist()) {
                return;
            }
            remove.removePersistence();
        }, "ExpireBytesPersistenceSolver::removePersistenceWithKey");
    }

    @Override
    public void removeSimilarKeyPersistence(@NotNull byte[] key) {
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
        }, "ExpireBytesPersistenceSolver::removePersistence");
    }

    @Override
    public void removeAllPersistence() {
        delAll();
    }
}
