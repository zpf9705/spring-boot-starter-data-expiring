package io.github.zpf9705.expiring.connection;

import io.github.zpf9705.expiring.command.ExpireCommands;
import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.core.proxy.JdkBeanDefinition;

/**
 * To simulate redis client connection abstract way, carry out a redis - spring encapsulation of learning
 * Here will command the classification, respectively for the implementation of the cache to better operation
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireConnection extends ExpireCommands, AutoCloseable, JdkBeanDefinition {

    /**
     * Get {@link ExpireStringCommands}.
     *
     * @return never {@literal null}.
     */
    default ExpireStringCommands stringCommands() {
        return this;
    }

    /**
     * Get {@link ExpireKeyCommands}.
     *
     * @return never {@literal null}.
     */
    default ExpireKeyCommands keyCommands() {
        return this;
    }
}