package io.github.zpf9705.expiring.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *
 * <p>
 *
 * @author zpf
 * @since jdk.spi.version-2022.11.18 - [2023-04-01 15:12]
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistenceExec {

    PersistenceExecTypeEnum value();
}
