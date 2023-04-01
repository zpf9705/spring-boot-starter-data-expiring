package io.github.zpf9705.expiring.connection.expiremap;


import io.github.zpf9705.expiring.connection.DefaultedExpireConnection;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of expire_map connection slot way
 * Main to Convenient the JDK dynamic proxy approach to the operation of the persistent object
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireMapConnectionSlot extends DefaultedExpireConnection {

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     */
    Boolean put(byte[] key, byte[] value);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object, long, TimeUnit)
     */
    Boolean putDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#putIfAbsent(Object, Object)
     */
    Boolean putIfAbsent(byte[] key, byte[] value);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#put(Object, Object)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    Boolean putIfAbsentDuration(byte[] key, byte[] value, Long duration, TimeUnit unit);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#get(Object)
     */
    byte[] getVal(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#replace(Object, Object)
     */
    byte[] replace(byte[] key, byte[] newValue);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object)
     */
    @Nullable
    Long deleteReturnSuccessNum(byte[]... keys);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#remove(Object, Object)
     */
    Map<byte[], byte[]> deleteSimilarKey(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#clear()
     */
    Boolean reboot();

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsKey(Object)
     */
    Boolean containsKey(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#containsValue(Object)
     */
    Boolean containsValue(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    Long getExpirationWithKey(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpiration(Object)
     */
    Long getExpirationWithDuration(byte[] key, TimeUnit unit);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    Long getExpectedExpirationWithKey(byte[] key);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#getExpectedExpiration(Object)
     */
    Long getExpectedExpirationWithUnit(byte[] key, TimeUnit unit);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#setExpiration(Object, long, TimeUnit)
     */
    void setExpirationDuration(byte[] key, Long duration, TimeUnit timeUnit);

    /*
     * (non-Javadoc)
     * @see net.jodah.expiringmap.ExpiringMap#resetExpiration(Object)
     */
    Boolean resetExpirationWithKey(byte[] key);
}
