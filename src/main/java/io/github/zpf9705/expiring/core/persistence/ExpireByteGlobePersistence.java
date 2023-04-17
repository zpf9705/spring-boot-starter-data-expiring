package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapCenter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Special extend for {@link ExpireSimpleGlobePersistence} with generic {@code byte[]}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireByteGlobePersistence extends ExpireSimpleGlobePersistence<byte[], byte[]> {

    private static final long serialVersionUID = 5518995337588214891L;

    public ExpireByteGlobePersistence() {
        super();
    }

    public ExpireByteGlobePersistence(Persistence<byte[], byte[]> persistence, String writePath) {
        super(persistence, writePath);
    }

    /**
     * Set in cache map {@code ExpireByteGlobePersistence} with {@code Entry<byte[], byte[]>} and {@code FactoryBeanName}
     * Whether you need to query in the cache
     *
     * @param entry must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetBytes(@NonNull Entry<byte[], byte[]> entry) {
        return ofSet(ExpireByteGlobePersistence.class, BytePersistence.class, entry);
    }

    /**
     * Set in cache map {@code ExpireByteGlobePersistence} with {@code Persistence<byte[],byte[]>}
     * Don't query is in the cache
     *
     * @param persistence must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetPersistenceBytes(@NonNull BytePersistence persistence) {
        return ofSetPersistence(ExpireByteGlobePersistence.class, persistence);
    }

    /**
     * Get an {@code ExpireByteGlobePersistence} with {@code key} and {@code value} in cache map
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofGetBytes(byte[] key, byte[] value) {
        return ofGet(key, value, ExpireByteGlobePersistence.class);
    }

    /**
     * Get an {@code ExpireByteGlobePersistence} with {@code key}  in cache map
     *
     * @param key must not be {@literal null}.
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofGetBytes(byte[] key) {
        return ofGet(key);
    }

    /**
     * Get any {@code ExpireByteGlobePersistence} with similar {@code key}  in cache map
     *
     * @param key must not be {@literal null}.
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static List<ExpireByteGlobePersistence> ofGetSimilarBytes(byte[] key) {
        return ofGetSimilar(key);
    }

    @Override
    public String getFactoryName() {
        return ExpireByteGlobePersistence.class.getName();
    }

    @Override
    public void deserializeWithEntry(@NonNull Persistence<byte[], byte[]> persistence,
                                     @NonNull String writePath) {
        //current time
        LocalDateTime now = LocalDateTime.now();
        //When the time is equal to or judged failure after now in record time
        if (persistence.getExpire() == null || now.isEqual(persistence.getExpire()) ||
                now.isAfter(persistence.getExpire())) {
            //Delete the persistent file
            del0(writePath);
            throw new PersistenceException("File [" + writePath + "] record time [" + persistence.getExpire() +
                    "] before or equals now");
        }
        //save key/value with byte[]
        Entry<byte[], byte[]> entry = persistence.getEntry();
        //check entry
        checkEntry(entry);
        //Remaining time is less than the standard not trigger persistence
        ExpireMapCenter.getExpireMapCenter().reload(entry.getKey(), entry.getValue(),
                condition(now, persistence.getExpire(), entry.getTimeUnit()),
                entry.getTimeUnit());
    }

    public static class BytePersistence extends Persistence<byte[], byte[]> {

        private static final long serialVersionUID = -368192386828860022L;

        public BytePersistence() {
            super();
        }

        public BytePersistence(Entry<byte[], byte[]> entry) {
            super(entry);
        }
    }
}
