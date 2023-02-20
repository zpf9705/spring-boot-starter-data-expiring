package io.github.zpf9705.core;

import org.springframework.core.NamedThreadLocal;

/**
 * save value in a litter time of context
 *
 * @author zpf
 * @see ThreadLocal {@link NamedThreadLocal}
 * @since 1.1.0
 */
public class ExpireContextHolder implements Clearable {

    private static final NamedThreadLocal<Object> KS_HOLDER = new NamedThreadLocal<>(
            "CURRENT EXPIRE KEY VALUE SAVE"
    );

    /**
     * save value of context
     *
     * @param value set current thread value
     */
    public static void temporary(Object value) {
        if (value == null) {
            return;
        }
        KS_HOLDER.set(value);
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
