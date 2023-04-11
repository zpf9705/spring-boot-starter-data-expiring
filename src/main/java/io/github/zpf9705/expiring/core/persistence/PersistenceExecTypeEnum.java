package io.github.zpf9705.expiring.core.persistence;

import java.util.concurrent.TimeUnit;

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
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthSimple) {
                solver.putPersistence(entry[indexOne], entry[indexTwo], null, null);
            } else if (entry.length == lengthDlg) {
                solver.putPersistence(entry[indexOne], entry[indexTwo],
                        (Long) entry[indexThree], (TimeUnit) entry[indexFour]);
            }
        }
    }, REPLACE_VALUE {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthGan) {
                solver.replaceValuePersistence(entry[indexOne], entry[indexTwo], entry[indexThree]);
            }
        }
    }, REPLACE_DURATION {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthDlg) {
                solver.replaceDurationPersistence(entry[indexOne], entry[indexTwo],
                                (Long) entry[indexThree], (TimeUnit) entry[indexFour]);
            }
        }
    }, REST_DURATION {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthSimple) {
                solver.restDurationPersistence(entry[indexOne], entry[indexTwo]);
            }
        }
    }, REMOVE_KEYS {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthSi) {
                solver.removePersistenceWithKeys(entry[indexOne]);
            }
        }
    }, REMOVE_TYPE {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry.length == lengthSi) {
                solver.removeSimilarKeyPersistence(entry[indexOne]);
            }
        }
    }, REMOVE_ALL {
        @Override
        public void dispose(PersistenceSolver solver, Object[] entry) {
            if (entry == null) {
                solver.removeAllPersistence();
            }
        }
    }
}
