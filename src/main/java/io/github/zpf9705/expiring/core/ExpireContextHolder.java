package io.github.zpf9705.expiring.core;

import org.springframework.core.NamedThreadLocal;

/**
 * The cache callback register context
 *
 * @author zpf
 * @since 1.1.0
 */
public class ExpireContextHolder {

    private static final NamedThreadLocal<Object> KS_HOLDER = new NamedThreadLocal<>(
            "CURRENT EXPIRE KEY VALUE SAVE"
    );

    /**
     * save value of context
     *
     * @param value set current thread value
     */
    public static void setValue(Object value) {
        if (value == null) {
            rest();
        } else {
            KS_HOLDER.set(value);
        }
    }

    /**
     * rest value of context
     */
    public static void rest() {
        KS_HOLDER.remove();
    }

    /**
     * get value of context
     *
     * @param runnable current execute runnable
     * @return current save value
     */
    public static Object gain(Runnable runnable) {
        Object acquire;
        try {
            runnable.run();
        } catch (Throwable e) {/*no logic*/} finally {
            acquire = KS_HOLDER.get();
            if (acquire != null) {
                rest();
            }
        }
        return acquire;
    }
}
