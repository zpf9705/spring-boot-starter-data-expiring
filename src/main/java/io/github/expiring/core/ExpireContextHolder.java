package io.github.expiring.core;

import org.springframework.core.NamedThreadLocal;

/**
 *
 *  save value in a litter time of context
 *
 * @see ThreadLocal {@link NamedThreadLocal}
 *
 * @author zpf
 * @since 1.1.0
 */
public class ExpireContextHolder implements Clearable {

    private static final NamedThreadLocal<Object> KS_HOLDER = new NamedThreadLocal<>(
            "CURRENT EXPIRE KEY VALUE SAVE"
    );

    /**
     * save value of context
     */
    public static void temporary(Object value) {
        if (value == null) {
            return;
        }
        KS_HOLDER.set(value);
    }

    /**
     * get value of context
     */
    public static Object gain(Runnable runnable) {
        Object acquire;
        try {
            runnable.run();
        } finally {
            acquire = KS_HOLDER.get();
            if (acquire != null) {
                KS_HOLDER.remove();
            }
        }
        return acquire;
    }

    /**
     * clean of context
     */
    @Override
    public void clear() {
        KS_HOLDER.remove();
    }
}
