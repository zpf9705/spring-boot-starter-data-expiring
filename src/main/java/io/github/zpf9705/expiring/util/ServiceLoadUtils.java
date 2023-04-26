package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Program interface loader tool, mainly used for the set of query interface implementation class
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ServiceLoadUtils<T> {

    private static final Map<String, ServiceLoader> LOAD_CACHE = new ConcurrentHashMap<>();

    private final ServiceLoader<T> load;

    public ServiceLoadUtils(ServiceLoader<T> load) {
        this.load = load;
    }

    /**
     * Use {@link java.util.ServiceLoader} to make the program interface implementation class load
     *
     * @param faceClass To load the interface
     * @param <T>       load type
     * @return this instance
     */
    public static <T> ServiceLoadUtils<T> load(@NotNull Class<T> faceClass) {
        ServiceLoader serviceLoader = LOAD_CACHE.get(faceClass.getName());
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(faceClass);
            //load cache Avoid reloading
            LOAD_CACHE.putIfAbsent(faceClass.getName(), serviceLoader);
            return new ServiceLoadUtils<>(serviceLoader);
        }
        return new ServiceLoadUtils<>(serviceLoader);
    }

    /**
     * To load all the program interface implementation class
     *
     * @return {@link java.util.Iterator}
     */
    public Iterator<T> loadAllSubInstance() {
        if (this.load == null) {
            return null;
        }
        return this.load.iterator();
    }

    /**
     * By class type for the specified type of interface implementation class type
     *
     * @param subClass specified type
     * @return specified sub
     */
    public T getSpecifiedServiceBySubClass(Class<? extends T> subClass) {
        return findBySubPredict(t -> t.getClass() == subClass);
    }

    /**
     * By class type for the specified type of interface implementation class name
     *
     * @param className specified type
     * @return specified sub
     */
    public T getSpecifiedServiceBySubClassName(String className) {
        return findBySubPredict(t -> t.getClass().getName().equals(className));
    }

    /**
     * Under conditions of assertions
     *
     * @param predicate {@link Predicate}
     * @return result
     */
    private T findBySubPredict(Predicate<T> predicate) {
        if (this.load == null) {
            return null;
        }
        for (T load : load) {
            if (predicate.test(load)) {
                return load;
            }
        }
        return null;
    }
}
