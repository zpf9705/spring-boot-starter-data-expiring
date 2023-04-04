package io.github.zpf9705.expiring.core;

import java.util.concurrent.TimeUnit;

/**
 * The cache persistence operation type
 *
 * @author zpf
 * @since 3.0.0
 */
public enum PersistenceExecTypeEnum implements Dispose {

    PUT {
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
    }, REPLACE {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthGan) {
                ExpirePersistenceUtils
                        .replacePersistence(entry[indexOne], entry[indexTwo], entry[indexThree]);
            }
        }
    }, SET_E {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthDlg) {
                ExpirePersistenceUtils
                        .setEPersistence(entry[indexOne], entry[indexTwo],
                                (Long) entry[indexThree], (TimeUnit) entry[indexFour]);
            }
        }
    }, REST {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSimple){
                ExpirePersistenceUtils.restPersistence(entry[indexOne], entry[indexTwo]);
            }
        }
    }, REMOVE {
        @Override
        public void dispose(Object[] entry) {
            if (entry.length == lengthSimple){
                ExpirePersistenceUtils.removePersistence(entry[indexOne], entry[indexTwo]);
            }
        }
    }, REMOVE_ANY {
        @Override
        public void dispose(Object[] entry) {

        }
    }
}
