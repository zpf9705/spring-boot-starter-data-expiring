package io.github.zpf9705.expiring.util;

import org.springframework.lang.NonNull;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Interface service load tools set
 *
 * @author zpf
 * @since 3.0.0
 */
public class ServiceLoadUtils<T> {

    private final ServiceLoader<T> load;

    public ServiceLoadUtils(Supplier<ServiceLoader<T>> load) {
        this.load = load.get();
    }

    public static <T> ServiceLoadUtils<T> load(@NonNull Class<T> faceClass) {
        return new ServiceLoadUtils<>(() -> ServiceLoader.load(faceClass));
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
            if (load.getClass() == subClass){
                return load;
            }
        }
        return null;
    }
}
