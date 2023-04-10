package io.github.zpf9705.expiring.util;

import org.springframework.lang.NonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface service load tools set
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public final class ServiceLoadUtils<T> {

    private static final Map<String, ServiceLoader> LOAD_CACHE = new ConcurrentHashMap<>();

    private final ServiceLoader<T> load;

    public ServiceLoadUtils(ServiceLoader<T> load) {
        this.load = load;
    }

    public static <T> ServiceLoadUtils<T> load(@NonNull Class<T> faceClass) {
        ServiceLoader serviceLoader = LOAD_CACHE.get(faceClass.getName());
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(faceClass);
            LOAD_CACHE.putIfAbsent(faceClass.getName(), serviceLoader);
            return new ServiceLoadUtils<>(serviceLoader);
        }
        return new ServiceLoadUtils<>(serviceLoader);
    }

    public Iterator<T> loadAllSubInstance() {
        if (this.load == null) {
            return null;
        }
        return this.load.iterator();
    }

    public T getSpecifiedServiceBySubClass(Class<? extends T> subClass) {
        if (this.load == null) {
            return null;
        }
        for (T load : load) {
            if (load.getClass() == subClass) {
                return load;
            }
        }
        return null;
    }
}
