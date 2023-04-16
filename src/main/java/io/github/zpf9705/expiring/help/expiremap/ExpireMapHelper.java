package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapKeyCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapStringCommands;
import io.github.zpf9705.expiring.help.AbstractExpireHelper;
import io.github.zpf9705.expiring.util.AssertUtils;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * {@code ExpireHelper} implementation of expire map.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapHelper extends AbstractExpireHelper implements ExpireMapHelperProxy {

    private final ExpiringMap<byte[], byte[]> expiringMap;

    private final ExpireMapByteContain contain;

    public ExpireMapHelper(ExpiringMap<byte[], byte[]> expiringMap) {
        AssertUtils.Operation.notNull(expiringMap, "ExpiringMap init no be null");
        this.expiringMap = expiringMap;
        this.contain = new ExpireMapByteContain(100);
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
    public Boolean put(byte[] key, byte[] value) {
        this.contain.addBytes(key, value);
        this.expiringMap.put(key, value);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    @Override
    public Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        this.contain.addBytes(key, value);
        this.expiringMap.put(key, value, duration, unit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    @Override
    public Boolean putIfAbsent(byte[] key, byte[] value) {
        if (this.contain.existKey(key)) return false;
        this.contain.addBytes(key,value);
        return this.expiringMap.put(key, value) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        if (this.contain.existKey(key)) return false;
        this.contain.addBytes(key,value);
        return this.expiringMap.put(key, value, duration, unit) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    @Override
    public byte[] getVal(byte[] key) {
        byte[] simpleBytesKey = this.contain.getSimilarBytesForKey(key);
        if (simpleBytesKey == null) return null;
        return this.expiringMap.get(simpleBytesKey);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    @Override
    public byte[] replace(byte[] key, byte[] newValue) {
        byte[] similarKey = this.contain.getSimilarBytesForKey(key);
        if (similarKey == null) return null;
        this.contain.put(similarKey, newValue);
        return this.expiringMap.replace(similarKey, newValue);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @Nullable
    @Override
    public Long deleteReturnSuccessNum(byte[]... keys) {
        long count = 0L;
        for (byte[] key : keys) {
            byte[] similarKey = this.contain.getSimilarBytesForKey(key);
            if (similarKey == null) {
                continue;
            }
            this.contain.remove(similarKey);
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
    public Map<byte[], byte[]> deleteSimilarKey(byte[] key) {
        Map<byte[], byte[]> map = new HashMap<>();
        List<byte[]> delKeys = new ArrayList<>();
        this.expiringMap.forEach((k, v) -> {
            if (this.SimilarJudgeOfBytes(k, key)) {
                map.put(k, v);
                delKeys.add(k);
            }
        });
        delKeys.forEach(this.expiringMap::remove);
        return map;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    @Override
    public Boolean reboot() {
        this.contain.clear();
        this.expiringMap.clear();
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    @Override
    public Boolean containsKey(byte[] key) {
        key = this.contain.getSimilarBytesForKey(key);
        if (key == null) return false;
        return this.expiringMap.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    @Override
    public Boolean containsValue(byte[] value) {
        value = this.contain.getSimilarBytesForValue(value);
        if (value == null) return false;
        return this.expiringMap.containsValue(value);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithKey(byte[] key) {
        key = this.contain.getSimilarBytesForKey(key);
        if (key == null) return null;
        return this.expiringMap.getExpiration(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithUnit(byte[] key, TimeUnit unit) {
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
        key = this.contain.getSimilarBytesForKey(key);
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
    public Boolean setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit) {
        key = this.contain.getSimilarBytesForKey(key);
        if (key == null) return false;
        this.expiringMap.setExpiration(key, duration, timeUnit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    @Override
    public Boolean resetExpirationWithKey(byte[] key) {
        key = this.contain.getSimilarBytesForKey(key);
        if (key == null) return false;
        this.expiringMap.resetExpiration(key);
        return true;
    }

    @Override
    public void restoreByteType(byte[] key, byte[] value) {
        this.contain.computeIfAbsent(key, v -> value);
    }
}
