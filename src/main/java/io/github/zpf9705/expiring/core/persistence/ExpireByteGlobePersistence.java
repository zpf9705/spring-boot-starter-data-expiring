package io.github.zpf9705.expiring.core.persistence;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapCenter;
import io.github.zpf9705.expiring.util.AssertUtils;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The only Special extend for {@link ExpireSimpleGlobePersistence} with generic {@code byte[]}
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
    public static ExpireByteGlobePersistence ofSetBytes(@NotNull Entry<byte[], byte[]> entry) {
        return ofSet(ExpireByteGlobePersistence.class, BytePersistence.class, entry);
    }

    /**
     * Set in cache map {@code ExpireByteGlobePersistence} with {@code Persistence<byte[],byte[]>}
     * Don't query is in the cache
     *
     * @param persistence must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetPersistenceBytes(@NotNull BytePersistence persistence) {
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
    public void deserializeWithString(@NotNull StringBuilder buffer) {
        super.deserializeWithString(buffer);
        //parse json
        Persistence<byte[], byte[]> persistence;
        try {
            persistence = JSONObject.parseObject(buffer.toString(), new TypeReference<BytePersistence>() {
            });
        } catch (Exception e) {
            throw new PersistenceException("Buffer data [" + buffer + " ] parse Persistence error " +
                    "[" + e.getMessage() + "]");
        }
        //No cache in the cache
        ExpireSimpleGlobePersistence<byte[], byte[]> of = ofSetPersistence(this.getClass(), persistence);
        AssertUtils.Persistence.notNull(of, "ExpireGlobePersistence no be null");
        this.deserializeWithEntry(persistence, of.getWritePath());
    }

    @Override
    public void deserializeWithEntry(@NotNull Persistence<byte[], byte[]> persistence,
                                     @NotNull String writePath) {
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
