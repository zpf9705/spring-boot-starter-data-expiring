package io.github.zpf9705.expiring.core.persistence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.zpf9705.expiring.core.ExpireTemplate;
import io.github.zpf9705.expiring.core.error.PersistenceException;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.util.AssertUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Special extend for {@link ExpireSimpleGlobePersistence} with generic {@code byte[]}
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
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
     * @param entry           must not be {@literal null}
     * @param factoryBeanName must not be {@literal null}
     * @return {@link ExpireByteGlobePersistence}
     */
    public static ExpireByteGlobePersistence ofSetBytes(@NonNull Entry<byte[], byte[]> entry,
                                                        @NonNull String factoryBeanName) {
        return ofSet(ExpireByteGlobePersistence.class, BytePersistence.class, entry, factoryBeanName);
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
    public void deserializeWithString(@NonNull StringBuilder buffer) {
        String json = buffer.toString();
        //check json
        AssertUtils.Persistence.isTrue(JSON.isValid(json), "Buffer data [" + json + "] no a valid json");
        //parse json
        BytePersistence persistence;
        try {
            persistence = JSONObject.parseObject(json, new TypeReference<BytePersistence>() {
            });
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
        //use help restore byte
        template.getHelperFactory().getHelper()
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

        public BytePersistence() {
            super();
        }

        public BytePersistence(Entry<byte[], byte[]> entry) {
            super(entry);
        }

        public BytePersistence(Entry<byte[], byte[]> entry, String factoryBeanName) {
            super(entry, factoryBeanName);
        }

        @Override
        public Persistence<byte[], byte[]> expireOn(@Nullable String factoryBeanName) {
            setFactoryBeanNameAndCheck(factoryBeanName);
            byte[] key = this.getEntry().getKey();
            //get template
            ExpireTemplate template = accessToTheCacheTemplate(getFactoryBeanName());
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
