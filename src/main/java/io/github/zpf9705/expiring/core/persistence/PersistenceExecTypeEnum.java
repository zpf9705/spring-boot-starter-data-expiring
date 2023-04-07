package io.github.zpf9705.expiring.core.persistence;

import java.util.concurrent.TimeUnit;

/**
 * The cache persistence operation type
 *
 * @author zpf
 * @since 3.0.0
 */
public enum PersistenceExecTypeEnum implements Dispose {

    SET {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSimple) {
                ExpirePersistenceUtils
                        .putPersistence(entry[indexOne], entry[indexTwo], null, null);
            } else if (entry.length == lengthDlg) {
                ExpirePersistenceUtils
                        .putPersistence(entry[indexOne], entry[indexTwo],
                                (Long) entry[indexThree], (TimeUnit) entry[indexFour]);
            }
        }
    }, REPLACE_VALUE {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthGan) {
                ExpirePersistenceUtils
                        .replaceValuePersistence(entry[indexOne], entry[indexTwo], entry[indexThree]);
            }
        }
    }, REPLACE_DURATION {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthDlg) {
                ExpirePersistenceUtils
                        .replaceDurationPersistence(entry[indexOne], entry[indexTwo],
                                (Long) entry[indexThree], (TimeUnit) entry[indexFour]);
            }
        }
    }, REST_DURATION {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSimple) {
                ExpirePersistenceUtils.restDurationPersistence(entry[indexOne], entry[indexTwo]);
            }
        }
    }, REMOVE_KEYS {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSi) {
                ExpirePersistenceUtils.removePersistenceWithKeys(entry[indexOne]);
            }
        }
    }, REMOVE_TYPE {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSi) {
                ExpirePersistenceUtils.removeSimilarKeyPersistence(entry[indexOne]);
            }
        }
    }, REMOVE_ALL {
        @Override
        public void dispose(Object[] entry) {
            if (entry == null) {
                ExpirePersistenceUtils.removeAllPersistence();
            }
        }
    };
}
