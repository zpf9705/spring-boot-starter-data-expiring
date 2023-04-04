package io.github.zpf9705.expiring.connection.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapKeyCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapStringCommands;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.persistence.PersistenceExecTypeEnum;
import io.github.zpf9705.expiring.util.AssertUtils;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@code ExpireConnection} implementation of expire_map.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapConnection implements ExpireMapConnectionSlot {

    private final ExpiringMap<byte[], byte[]> expiringMap;

    private final ExpireMapByteContain containOfKey;

    private final ExpireMapByteContain containOfValue;

    public ExpireMapConnection(ExpiringMap<byte[], byte[]> expiringMap) {
        AssertUtils.Operation.notNull(expiringMap, "ExpiringMap init no be null");
        this.expiringMap = expiringMap;
        this.containOfKey = new ExpireMapByteContain(100);
        this.containOfValue = new ExpireMapByteContain(100);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#stringCommands()
     */
    @Override
    public ExpireStringCommands stringCommands() {
        return new ExpireMapStringCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.connection.ExpireConnection#keyCommands()
     */
    @Override
    public ExpireKeyCommands keyCommands() {
        return new ExpireMapKeyCommands(this);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean put(byte[] key, byte[] value) {
        this.containOfKey.addBytes(key);
        this.containOfValue.addBytes(value);
        this.expiringMap.put(key, value);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        this.containOfKey.addBytes(key);
        this.containOfValue.addBytes(value);
        this.expiringMap.put(key, value, duration, unit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean putIfAbsent(byte[] key, byte[] value) {
        if (this.containOfKey.exist(key)) return false;
        this.containOfKey.addBytes(key);
        this.containOfValue.addBytes(value);
        return this.expiringMap.put(key, value) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        if (this.containOfKey.exist(key)) return false;
        this.containOfKey.add(key);
        this.containOfValue.add(value);
        return this.expiringMap.put(key, value, duration, unit) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    @Override
    public byte[] getVal(byte[] key) {
        byte[] simpleBytesKey = this.containOfKey.getSimilarBytes(key);
        if (simpleBytesKey == null) return null;
        return this.expiringMap.get(simpleBytesKey);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REPLACE)
    public byte[] replace(byte[] key, byte[] newValue) {
        byte[] similarKey = this.containOfKey.getSimilarBytes(key);
        if (similarKey == null) return null;
        byte[] oldValue = this.expiringMap.get(similarKey);
        if (oldValue != null) {
            this.containOfValue.remove(oldValue);
        }
        return this.expiringMap.replace(similarKey, newValue);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @Nullable
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REMOVE_ANY)
    public Long deleteReturnSuccessNum(byte[]... keys) {
        long count = 0L;
        for (byte[] key : keys) {
            byte[] similarKey = this.containOfKey.getSimilarBytes(key);
            if (similarKey == null) {
                continue;
            }
            if (this.expiringMap.remove(similarKey) != null) {
                count++;
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REMOVE)
    public Map<byte[], byte[]> deleteSimilarKey(byte[] key) {
        if (!hasKey(key)) {
            return Collections.emptyMap();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REMOVE)
    public Boolean reboot() {
        this.expiringMap.clear();
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    @Override
    public Boolean containsKey(byte[] key) {
        key = this.containOfKey.getSimilarBytes(key);
        if (key == null) return false;
        return this.expiringMap.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    @Override
    public Boolean containsValue(byte[] value) {
        value = this.containOfValue.getSimilarBytes(value);
        if (value == null) return false;
        return this.expiringMap.containsValue(value);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithKey(byte[] key) {
        key = this.containOfKey.getSimilarBytes(key);
        if (key == null) return null;
        return this.expiringMap.getExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithDuration(byte[] key, TimeUnit unit) {
        Long expiration = this.getExpirationWithKey(key);
        if (expiration == null) return null;
        return TimeUnit.MICROSECONDS.convert(expiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithKey(byte[] key) {
        key = this.containOfKey.getSimilarBytes(key);
        if (key == null) return null;
        return this.expiringMap.getExpectedExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit) {
        Long expectedExpiration = this.getExpectedExpirationWithKey(key);
        if (expectedExpiration == null) return null;
        return TimeUnit.MICROSECONDS.convert(expectedExpiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.SET_E)
    public Boolean setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit) {
        key = this.containOfKey.getSimilarBytes(key);
        if (key == null) return false;
        this.expiringMap.setExpiration(key, duration, timeUnit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REST)
    public Boolean resetExpirationWithKey(byte[] key) {
        key = this.containOfKey.getSimilarBytes(key);
        if (key == null) return false;
        this.expiringMap.resetExpiration(key);
        return true;
    }

    @Override
    public void restoreByteType(byte[] key, byte[] value) {
        this.containOfKey.computeIfAbsent(key);
        this.containOfValue.computeIfAbsent(value);
    }
}
