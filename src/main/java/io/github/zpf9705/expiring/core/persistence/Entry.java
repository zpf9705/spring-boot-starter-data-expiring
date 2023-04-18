package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Persistent cache category
 *
 * @author zpf
 * @since 1.1.0
 **/
@Data
public class Entry<K, V> implements Serializable {

    private static final long serialVersionUID = -8927814512833346379L;
    private K key;
    private V value;
    private Long duration;
    private TimeUnit timeUnit;

    public boolean haveDuration() {
        return duration != null && duration != 0L && timeUnit != null;
    }

    public Entry(K key, V value) {
        this(key, value, null, null);
    }

    public Entry(K key, V value, Long duration, TimeUnit timeUnit) {
        this.key = key;
        this.value = value;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public void refreshOfExpire(@NotNull Long duration, @NotNull TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public static <K, V> Entry<K, V> of(K key, V value) {
        return new Entry<>(key, value);
    }

    public static <K, V> Entry<K, V> of(K key, V value, Long duration, TimeUnit timeUnit) {
        return new Entry<>(key, value, duration, timeUnit);
    }
}
