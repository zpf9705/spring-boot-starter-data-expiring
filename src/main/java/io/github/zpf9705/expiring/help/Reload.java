package io.github.zpf9705.expiring.help;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The cache to reload the client select annotations
 * <p>
 * {@link #client()} ()} The client name
 *
 * @author zpf
 * @since 3.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reload {

    String client();
}
