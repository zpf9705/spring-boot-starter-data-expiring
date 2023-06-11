package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.NotNull;

/**
 * The cache persistence operation type
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public enum PersistenceExecTypeEnum implements Dispose {

    SET {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.putPersistence(variable.getKey(),
                    variable.getValue(),
                    variable.getDuration(),
                    variable.getUnit());
        }
    }, REPLACE_VALUE {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.replaceValuePersistence(variable.getKey(),
                    variable.getNewValue());
        }
    }, REPLACE_DURATION {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.replaceDurationPersistence(variable.getKey(),
                    variable.getDuration(),
                    variable.getUnit());
        }
    }, REST_DURATION {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.restDurationPersistence(variable.getKey());
        }
    }, REMOVE_KEYS {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            for (Object key : variable.getAnyKeys()) {
                solver.removePersistenceWithKey(key);
            }
        }
    }, REMOVE_TYPE {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.removeSimilarKeyPersistence(variable.getKey());
        }
    }, REMOVE_ALL {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable) {
            solver.removeAllPersistence();
        }
    };

    @Override
    public PersistenceExecTypeEnum getExecType() {
        return this;
    }
}
