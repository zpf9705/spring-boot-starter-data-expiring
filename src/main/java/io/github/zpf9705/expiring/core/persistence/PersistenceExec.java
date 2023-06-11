package io.github.zpf9705.expiring.core.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Persistent implementation way annotation
 *
 * @author zpf
 * @since 3.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistenceExec {

    /**
     * Perform annotation type
     *
     * @return {@link PersistenceExecTypeEnum}
     */
    PersistenceExecTypeEnum value();

    /**
     * The processing of execution annotation corresponding interface implementation class type
     *
     * @return {@link PersistenceSolver}
     */
    Class<? extends PersistenceSolver> shouldSolver() default ExpireBytesPersistenceSolver.class;

    /**
     * Perform expectations
     *
     * @return result value
     */
    ValueExpectations expectValue() default ValueExpectations.NULL;


    @SuppressWarnings("rawtypes")
    enum ValueExpectations implements Predicate<Object> {

        NULL(null),

        NOT_NULL(v -> v != null),

        LONG_NO_ZERO(v -> {
            if (!NOT_NULL.predicate.test(v) || !(v instanceof Long)) {
                return false;
            }
            return (Long) v > 0L;
        }),

        NOT_EMPTY(v -> {
            if (!NOT_NULL.predicate.test(v)) {
                return false;
            }
            if (v instanceof Collection) {
                return !((Collection) v).isEmpty();
            } else if (v instanceof Map) {
                return !((Map) v).isEmpty();
            }
            return false;
        }),

        REALLY(v -> {
            if (!NOT_NULL.predicate.test(v) || !(v instanceof Boolean)) {
                return false;
            }
            return ((Boolean) v);
        });

        private final Predicate<Object> predicate;

        @Override
        public boolean test(Object o) {
            Predicate<Object> pre = this.predicate;
            if (pre == null) {
                return true;
            }
            return pre.test(o);
        }

        ValueExpectations(Predicate<Object> predicate) {
            this.predicate = predicate;
        }
    }
}
