package io.github.zpf9705.expiring.connection.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapKeyCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapStringCommands;
import io.github.zpf9705.expiring.core.PersistenceExec;
import io.github.zpf9705.expiring.core.PersistenceExecTypeEnum;
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

    public ExpireMapConnection(ExpiringMap<byte[], byte[]> expiringMap) {
        this.expiringMap = expiringMap;
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
        expiringMap.put(key, value, duration, unit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean putIfAbsent(byte[] key, byte[] value) {
        byte[] bytes = this.expiringMap.putIfAbsent(key, value);
        return bytes == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.PUT)
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        if (Boolean.TRUE.equals(this.putIfAbsent(key, value))) {
            this.setExpiration(key, duration, unit);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    @Override
    public byte[] getVal(byte[] key) {
        return this.expiringMap.get(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REPLACE)
    public byte[] replace(byte[] key, byte[] newValue) {
        return this.expiringMap.replace(key, newValue);
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
            if (this.expiringMap.remove(key) != null) {
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
        return this.expiringMap.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    @Override
    public Boolean containsValue(byte[] key) {
        return this.expiringMap.containsValue(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithKey(byte[] key) {
        return this.expiringMap.getExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithDuration(byte[] key, TimeUnit unit) {
        Long expiration = this.getExpiration(key);
        if (expiration == null) {
            return null;
        }
        return TimeUnit.MICROSECONDS.convert(expiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithKey(byte[] key) {
        return this.expiringMap.getExpectedExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit) {
        Long expectedExpiration = this.getExpectedExpiration(key);
        if (expectedExpiration == null) {
            return null;
        }
        return TimeUnit.MICROSECONDS.convert(expectedExpiration, unit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.SET_E)
    public void setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit) {
        this.expiringMap.setExpiration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REST)
    public Boolean resetExpirationWithKey(byte[] key) {
        this.expiringMap.resetExpiration(key);
        return true;
    }
}
