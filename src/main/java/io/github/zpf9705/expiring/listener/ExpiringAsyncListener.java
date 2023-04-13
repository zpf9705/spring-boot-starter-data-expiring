package io.github.zpf9705.expiring.listener;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Asynchronous listener
 * <pre>
 *     {@code
 *     @ExpiringAsyncListener
 *     public class ExpiringListenerManager extends MessageExpiringContainer {
 *
 *     @Override
 *     public void onMessage(Message message) {
 *         GeneralLog.info("Cache expiration key [{}]", message.getKey());
 *     }
 * }
 *     }
 * </pre>
 *
 * @author zpf
 * @since 3.0.0
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiringAsyncListener {
}
