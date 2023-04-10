package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.core.persistence.ExpireBytesPersistenceSolver;
import io.github.zpf9705.expiring.core.persistence.PersistenceSolver;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;
import net.jodah.expiringmap.ExpirationListener;

/**
 * default Expiring Load Listener container of key{@code  Object} and value {@code Object}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class MessageExpiringContainer implements ExpirationListener<byte[], byte[]> {

    @Override
    public void expired(byte[] key, byte[] value) {
        onMessage(Message.serial(key, value));
        //clean Persistence with key and value
        cleanPersistence(key,value);
    }

    @SuppressWarnings("unchecked")
    public void cleanPersistence(byte[] key, byte[] value) {
        PersistenceSolver<byte[], byte[]> solver = ServiceLoadUtils.load(PersistenceSolver.class)
                .getSpecifiedServiceBySubClass(ExpireBytesPersistenceSolver.class);
        if (solver != null) {
            solver.removePersistence(key, value);
        }
    }

    /**
     * Will into a byte array to {@link Message}
     *
     * @param message be a {@code message}
     */
    public abstract void onMessage(Message message);
}
