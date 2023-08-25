package io.github.zpf9705.expiring.core.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierNickname;
import javax.annotation.meta.When;
import java.lang.annotation.*;

/**
 * A null value validation annotation is annotated to represent the existence of nullable values.
 * {@literal Applied to standards compliant with JSR350.}
 *
 * @author zpf
 * @since 3.0.0
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull(when = When.MAYBE)
@TypeQualifierNickname
public @interface CanNull {
}
