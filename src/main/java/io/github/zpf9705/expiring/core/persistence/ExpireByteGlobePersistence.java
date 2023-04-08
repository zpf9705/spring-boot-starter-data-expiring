package io.github.zpf9705.expiring.core.persistence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.zpf9705.expiring.core.ExpireTemplate;
import io.github.zpf9705.expiring.core.error.PersistenceException;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;

/**
 * Special extend for {@link ExpireSimpleGlobePersistence} with generic {@code byte[]}
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExpireByteGlobePersistence extends ExpireSimpleGlobePersistence<byte[], byte[]> {

    private static final long serialVersionUID = 5518995337588214891L;

    public ExpireByteGlobePersistence(Persistence<byte[], byte[]> persistence, String writePath) {
        super(persistence, writePath);
    }

    public ExpireByteGlobePersistence(Supplier<Persistence<byte[], byte[]>> persistence, String writePath) {
        super(persistence, writePath);
    }

    /**
     * Set in cache map {@code ExpireByteGlobePersistence} with {@code Entry<byte[], byte[]>} and {@code FactoryBeanName}
     * Whether need to query in the cache
     *
     * @param entry           must not be {@literal null}
     * @param factoryBeanName must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetBytes(@NonNull Entry<byte[], byte[]> entry,
                                                        @NonNull String factoryBeanName) {
        return convert(() -> ofSet(entry, factoryBeanName));
    }

    /**
     * Set in cache map {@code ExpireByteGlobePersistence} with {@code Persistence<byte[],byte[]>}
     * Don't query is in the cache
     *
     * @param persistence must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetPersistenceBytes(@NonNull BytePersistence persistence) {
        return convert(() -> ofSetPersistence(persistence));
    }

    /**
     * Get an {@code ExpireByteGlobePersistence} with {@code key} and {@code value} in cache map
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofGetBytes(byte[] key, byte[] value) {
        return convert(() -> ofGet(key, value));
    }

    /**
     * Get an {@code ExpireByteGlobePersistence} with {@code key}  in cache map
     *
     * @param key must not be {@literal null}.
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofGetBytes(byte[] key) {
        return ofGetBytes(key, null);
    }

    /**
     * Get any {@code ExpireByteGlobePersistence} with similar {@code key}  in cache map
     *
     * @param key must not be {@literal null}.
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static List<ExpireByteGlobePersistence> ofGetSimilarBytes(byte[] key) {
        return null;
    }

    /**
     * Convert {@code ExpireSimpleGlobePersistence<byte[],byte[]> simpleGlobePersistence} to {@code ExpireByteGlobePersistence}
     *
     * @param simpleGlobePersistence must not be {@literal null}
     * @return {@link ExpireSimpleGlobePersistence}
     */
    public static ExpireByteGlobePersistence convert(@NonNull Supplier<ExpireSimpleGlobePersistence<byte[], byte[]>>
                                                             simpleGlobePersistence) {
        if (simpleGlobePersistence.get() == null) {
            return null;
        }
        return (ExpireByteGlobePersistence) simpleGlobePersistence.get();
    }

    @Override
    public void deserializeWithString(@NonNull StringBuilder buffer) {
        String json = buffer.toString();
        //check json
        AssertUtils.Persistence.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
        //parse json
        BytePersistence persistence;
        try {
            persistence = JSONObject.parseObject(json, new TypeReference<BytePersistence>() {});
        } catch (Exception e) {
            throw new PersistenceException("Buffer data [" + json + " ] parse Persistence error " +
                    "[" + e.getMessage() + "]");
        }
        //No cache in the cache
        ExpireByteGlobePersistence of = ofSetPersistenceBytes(persistence);
        AssertUtils.Persistence.notNull(of, "ExpireGlobePersistence no be null");
        //record [op bean]
        ExpireTemplate template = accessToTheCacheTemplate(persistence.getFactoryBeanName());
        this.deserializeWithTemplate(template, persistence, of.getWritePath());
    }

    @Override
    public void deserializeWithTemplate(@NonNull ExpireTemplate template,
                                        @NonNull Persistence<byte[], byte[]> persistence,
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
        //serializer key restore
        Object key = template.getKeySerializer().deserialize(entry.getKey());
        //serializer value restore
        Object value = template.getValueSerializer().deserialize(entry.getValue());
        //use connection restore byte
        template.getConnectionFactory().getConnection()
                .restoreByteType(entry.getKey(), entry.getValue());
        //Remaining time is less than the standard not trigger persistence
        template.opsForValue().set(key, value,
                condition(now, persistence.getExpire(), entry.getTimeUnit()),
                entry.getTimeUnit());
        //Print successfully restore cached information
        Console.info("Cache Key [{}] operation has been restored to the memory", key);
    }

    public static class BytePersistence extends Persistence<byte[], byte[]> {

        private static final long serialVersionUID = -368192386828860022L;

        @Override
        public Persistence<byte[], byte[]> expireOn(@NonNull String factoryBeanName) {
            byte[] key = this.getEntry().getKey();
            //get template
            ExpireTemplate template = accessToTheCacheTemplate(factoryBeanName);
            //The underlying the use of basic data types in upper byte type
            Object deKey = template.getKeySerializer().deserialize(key);
            AssertUtils.Persistence.isTrue(template.exist(deKey),
                    "Key [" + Arrays.toString(key) + "] no exist in cache");
            //For the rest of the due millisecond value
            Long expectedExpiration = template.opsExpirationOperations().getExpectedExpiration(deKey);
            AssertUtils.Persistence.isTrue(expectedExpiration != null && expectedExpiration != 0,
                    "Expected Expiration null or be zero");
            //give expire
            this.setExpire(LocalDateTime.now().plus(expectedExpiration, ChronoUnit.MILLIS));
            this.setFactoryBeanName(factoryBeanName);
            return this;
        }
    }
}
