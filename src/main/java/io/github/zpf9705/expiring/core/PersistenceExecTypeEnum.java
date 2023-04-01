package io.github.zpf9705.expiring.core;

/**
 * The cache persistence operation type
 *
 * @author zpf
 * @since 3.0.0
 */
public enum PersistenceExecTypeEnum implements Dispose{

    PUT{
        @Override
        public void dispose(Object[] args) {
        }
    }, REPLACE{
        @Override
        public void dispose(Object[] args) {
        }
    }, SET_E{
        @Override
        public void dispose(Object[] args) {

        }
    }, REST{
        @Override
        public void dispose(Object[] args) {

        }
    }, REMOVE{
        @Override
        public void dispose(Object[] args) {

        }
    },REMOVE_ANY{
        @Override
        public void dispose(Object[] args) {

        }
    }
}
