package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapKeyCommands;
import io.github.zpf9705.expiring.command.expiremap.ExpireMapStringCommands;
import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.AbstractExpireHelper;
import net.jodah.expiringmap.ExpiringMap;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * {@code ExpireMapRealHelper} implementation of ExpireMapHelper.
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapRealHelper extends AbstractExpireHelper implements ExpireMapHelper {

    private final ExpireMapCenter center;

    public ExpireMapRealHelper(@NotNull Supplier<ExpireMapCenter> centerSupplier) {
        this.center = centerSupplier.get();
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
        contain().addBytes(key, value);
        expire().put(key, value);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    @Override
    public Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        contain().addBytes(key, value);
        expire().put(key, value, duration, unit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    @Override
    public Boolean putIfAbsent(byte[] key, byte[] value) {
        if (contain().existKey(key)) return false;
        contain().addBytes(key, value);
        return expire().put(key, value) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    @Override
    public Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        if (contain().existKey(key)) return false;
        contain().addBytes(key, value);
        return expire().put(key, value, duration, unit) == null;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    @Override
    public byte[] getVal(byte[] key) {
        byte[] simpleBytesKey = contain().getSimilarBytesForKey(key);
        if (simpleBytesKey == null) return null;
        return expire().get(simpleBytesKey);
    }

    @Override
    public List<byte[]> getKeysByKeys(byte[] key) {
        byte[] simpleBytesKey = contain().getSimilarBytesForKey(key);
        if (simpleBytesKey == null) return null;
        return expire().keySet().stream().map(dai -> {
            if (this.similarJudgeOfBytes(dai, key)) {
                return dai;
            }
            return null;
        }).collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    @Override
    public byte[] replace(byte[] key, byte[] newValue) {
        byte[] similarKey = contain().getSimilarBytesForKey(key);
        if (similarKey == null) {
            // How did not directly put in
            this.put(key, newValue);
            return newValue;
        }
        //Delete the old value
        contain().remove(similarKey);
        //In the new value
        contain().put(similarKey, newValue);
        //Replace the new value and return old value
        return expire().replace(similarKey, newValue);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @CanNull
    @Override
    public Long deleteReturnSuccessNum(byte[]... keys) {
        long count = 0L;
        for (byte[] key : keys) {
            byte[] similarKey = contain().getSimilarBytesForKey(key);
            if (similarKey == null) {
                continue;
            }
            contain().remove(similarKey);
            if (expire().remove(similarKey) != null) {
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
        expire().forEach((k, v) -> {
            if (this.similarJudgeOfBytes(k, key)) {
                map.put(k, v);
                delKeys.add(k);
            }
        });
        delKeys.forEach(k -> {
            expire().remove(k);
            contain().remove(k);
        });
        return map;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    @Override
    public Boolean reboot() {
        contain().clear();
        expire().clear();
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    @Override
    public Boolean containsKey(byte[] key) {
        key = contain().getSimilarBytesForKey(key);
        if (key == null) return false;
        return expire().containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    @Override
    public Boolean containsValue(byte[] value) {
        value = contain().getSimilarBytesForValue(value);
        if (value == null) return false;
        return expire().containsValue(value);
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    @Override
    public Long getExpirationWithKey(byte[] key) {
        key = contain().getSimilarBytesForKey(key);
        if (key == null) return null;
        return expire().getExpiration(key);
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
        key = contain().getSimilarBytesForKey(key);
        if (key == null) return null;
        return expire().getExpectedExpiration(key);
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
        key = contain().getSimilarBytesForKey(key);
        if (key == null) return false;
        expire().setExpiration(key, duration, timeUnit);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    @Override
    public Boolean resetExpirationWithKey(byte[] key) {
        key = contain().getSimilarBytesForKey(key);
        if (key == null) return false;
        expire().resetExpiration(key);
        return true;
    }

    @Override
    public void restoreByteType(byte[] key, byte[] value) {
        throw new OperationsException();
    }

    public ExpiringMap<byte[], byte[]> expire() {
        return this.center.getExpiringMap();
    }

    public ExpireMapByteContain contain() {
        return this.center.getContain();
    }
}
