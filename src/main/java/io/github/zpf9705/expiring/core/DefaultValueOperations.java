package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.help.ExpireHelper;
import java.util.concurrent.TimeUnit;


/**
 * The default value operations implementation class for {@link ValueOperations}
 *
 * @author zpf
 * @since 1.1.0
 */
public class DefaultValueOperations<K, V> extends AbstractOperations<K, V> implements ValueOperations<K, V> {

    public DefaultValueOperations(ExpireTemplate<K, V> expireTemplate) {
        super(expireTemplate);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object)
     */
    public void set(K key, V value) {

        final byte[] rawValue = this.rawValue(value);
        this.execute(new AbstractOperations<K, V>.ValueDeserializingExpireCallback(key) {
            @Override
            protected byte[] inExpire(byte[] rawKey, ExpireHelper helper) {
                helper.set(rawKey, rawValue);
                return null;
            }
        }, true,false);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object, Long, TimeUnit)
     */
    public void set(K key, V value, Long duration, TimeUnit unit) {

        final byte[] rawValue = this.rawValue(value);
        this.execute(new AbstractOperations<K, V>.ValueDeserializingExpireCallback(key) {
            @Override
            protected byte[] inExpire(byte[] rawKey, ExpireHelper helper) {
                helper.setE(rawKey, rawValue, duration, unit);
                return null;
            }
        }, true,false);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#setIfAbsent(Object, Object)
     */
    @Override
    public Boolean setIfAbsent(K key, V value) {
        byte[] rawKey = this.rawKey(key);
        byte[] rawValue = this.rawValue(value);
        return this.execute((connection) -> connection.setNX(rawKey, rawValue),
                true,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#setIfAbsent(Object, Object, Long, TimeUnit)
     */
    @Override
    public Boolean setIfAbsent(K key, V value, Long duration, TimeUnit unit) {

        byte[] rawKey = this.rawKey(key);
        byte[] rawValue = this.rawValue(value);
        return this.execute((connection) -> connection.setEX(rawKey, rawValue, duration, unit),
                true,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#get(Object)
     */
    public V get(K key) {

        return this.execute(new ValueDeserializingExpireCallback(key) {
            @Override
            protected byte[] inExpire(byte[] rawKey, ExpireHelper helper) {
                return helper.get(rawKey);
            }
        }, false,true);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.core.ValueOperations#getAndSet(Object, Object)
     */
    @Override
    public V getAndSet(K key, V newValue) {

        final byte[] rawValue = this.rawValue(newValue);
        return this.execute(new ValueDeserializingExpireCallback(key) {
            @Override
            protected byte[] inExpire(byte[] rawKey, ExpireHelper helper) {
                return helper.getAndSet(rawKey, rawValue);
            }
        }, false,true);
    }
}
