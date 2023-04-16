package io.github.zpf9705.expiring.help;

import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Default Expire Helper for interface
 *
 * @author zpf
 * @since 3.0.0
 */
public interface DefaultedExpireHelper extends ExpireHelper {

    @Nullable
    @Override
    default Long delete(byte[]... keys) {
        return keyCommands().delete(keys);
    }

    @Override
    default Map<byte[], byte[]> deleteType(byte[] key) {
        return keyCommands().deleteType(key);
    }

    @Override
    default Boolean deleteAll() {
        return keyCommands().deleteAll();
    }

    @Override
    default Boolean hasKey(byte[] key) {
        return keyCommands().hasKey(key);
    }

    @Nullable
    @Override
    default Long getExpiration(byte[] key) {
        return keyCommands().getExpiration(key);
    }

    @Nullable
    @Override
    default Long getExpiration(byte[] key, TimeUnit unit) {
        return keyCommands().getExpiration(key, unit);
    }

    @Nullable
    @Override
    default Long getExpectedExpiration(byte[] key) {
        return keyCommands().getExpectedExpiration(key);
    }

    @Nullable
    @Override
    default Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return keyCommands().getExpectedExpiration(key, unit);
    }

    @Override
    default Boolean setExpiration(byte[] key, Long duration, TimeUnit unit) {
        return keyCommands().setExpiration(key, duration, unit);
    }

    @Override
    default Boolean resetExpiration(byte[] key) {
        return keyCommands().resetExpiration(key);
    }

    @Nullable
    @Override
    default Boolean set(byte[] key, byte[] value) {
        return stringCommands().set(key, value);
    }

    @Override
    default Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return stringCommands().setE(key, value, duration, unit);
    }

    @Nullable
    @Override
    default Boolean setNX(byte[] key, byte[] value) {
        return stringCommands().setNX(key, value);
    }

    @Nullable
    @Override
    default Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return stringCommands().setEX(key, value, duration, unit);
    }

    @Nullable
    @Override
    default byte[] get(byte[] key) {
        return stringCommands().get(key);
    }

    @Nullable
    @Override
    default byte[] getAndSet(byte[] key, byte[] newValue) {
        return stringCommands().getAndSet(key, newValue);
    }

    @Override
    default void close() {
        throw new UnsupportedOperationException();
    }
}
