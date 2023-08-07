package io.github.zpf9705.expiring.help;

import java.io.Serializable;

/**
 * This abstract class is used to record the currently activated central class
 * and provide it to the corresponding implementation method of the central class
 *
 * @author zpf
 * @since 3.0.5
 */
@SuppressWarnings("rawtypes")
public abstract class RecordActivationCenter<C, K, V> implements Center<C, K, V>, Serializable {

    private static final long serialVersionUID = 3742788944739950396L;

    static Center center;

    /**
     * Placing a single instance cache
     *
     * @param singleton can be {@literal null}
     */
    public static synchronized void setSingletonCenter(Center singleton) {
        if (center != null || singleton == null) {
            return;
        }
        center = singleton;
    }

    /**
     * Obtain the currently activated center
     *
     * @return center not be {@literal null}
     */
    public static synchronized Center getSingletonCenter() {
        return center;
    }
}
