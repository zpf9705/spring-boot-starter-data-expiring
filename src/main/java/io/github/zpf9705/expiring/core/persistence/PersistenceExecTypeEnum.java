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
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable variable = convert(this, entry);
            solver.putPersistence(variable.getKey(),
                    variable.getValue(),
                    variable.getDuration(),
                    variable.getUnit());
        }
    }, REPLACE_VALUE {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            solver.replaceValuePersistence(convert.getKey(),
                    convert.getNewValue());
        }
    }, REPLACE_DURATION {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            solver.replaceDurationPersistence(convert.getKey(),
                    convert.getDuration(),
                    convert.getUnit());
        }
    }, REST_DURATION {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            solver.restDurationPersistence(convert.getKey());
        }
    }, REMOVE_KEYS {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            for (Object key : convert.getAnyKeys()) {
                solver.removePersistenceWithKey(key);
            }
        }
    }, REMOVE_TYPE {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            solver.removeSimilarKeyPersistence(convert.getKey());
        }
    }, REMOVE_ALL {
        @Override
        public void dispose(@NotNull PersistenceSolver solver, Object[] entry) {
            DisposeVariable convert = convert(this, entry);
            if (convert == null) {
                solver.removeAllPersistence();
            }
        }
    }
}
