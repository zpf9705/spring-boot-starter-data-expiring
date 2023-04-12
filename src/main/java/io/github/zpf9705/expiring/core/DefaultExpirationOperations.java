package io.github.zpf9705.expiring.core;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation of {@link ExpirationOperations}.
 *
 * @author zpf
 * @since 3.0.0
 */
public class DefaultExpirationOperations<K, V> extends AbstractOperations<K, V> implements ExpirationOperations<K, V> {

    DefaultExpirationOperations(ExpireTemplate<K, V> expireTemplate) {
        super(expireTemplate);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpiration(Object)
     */
    @Override
    public Long getExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.getExpiration(rawKey),
                false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object, TimeUnit)
     */
    @Override
    public Long getExpiration(K key, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.getExpiration(rawKey, unit),
                false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object)
     */
    @Override
    public Long getExpectedExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.getExpectedExpiration(rawKey),
                false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#getExpectedExpiration(Object, TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(K key, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.getExpectedExpiration(rawKey, unit),
                false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#setExpiration(Object, Long, TimeUnit)
     */
    @Override
    public Boolean setExpiration(K key, Long duration, TimeUnit unit) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.setExpiration(rawKey, duration, unit),
                false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ExpirationOperations#resetExpiration(java.lang.Object)
     */
    @Override
    public Boolean resetExpiration(K key) {
        byte[] rawKey = this.rawKey(key);
        return this.execute((connection) -> connection.resetExpiration(rawKey),
                false,true);
    }
}
