package io.github.zpf9705.expiring.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Synchronous listener
 * <pre>
 *     {@code
 *     {@code @ExpiringSyncListener}
 *     public class ExpiringListenerManager extends MessageExpiringContainer {
 *
 *     {@code @Override}
 *     public void onMessage(Message message) {
 *         GeneralLog.info("Cache expiration key [{}]", message.getKey());
 *     }
 * }
 *     }
 * </pre>
 *
 * @author zpf
 * @since 1.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiringSyncListener {
}
