package io.github.zpf9705.core;

import java.util.List;
import java.util.Map;

/**
 * Keep clear of in time space
 *
 * @author zpf
 * @since 1.1.0
 */
public interface Clearable {

    /**
     * this.xxx = null
     * @see Map#clear()
     * @see List#clear()
     * @see Map.Entry#clear()
     */
    void clear();
}
