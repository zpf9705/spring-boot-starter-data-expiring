package io.github.zpf9705.expiring.listener;

import io.github.zpf9705.expiring.core.logger.Console;

/**
 * Default for {@link MessageExpiringListener}
 *
 * @author zpf
 * @since 3.0.0
 */
public class DefaultMessageExpiringListener extends MessageExpiringListener {

    @Override
    public void onMessage(Message message) {
        Console.info("Default Message Listener expired : [Key] : {}", message.getKey());
    }
}
