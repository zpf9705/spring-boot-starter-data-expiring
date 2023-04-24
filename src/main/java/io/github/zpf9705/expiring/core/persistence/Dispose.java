package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The cache persistence operation interface for {@link PersistenceSolver}
 * <ul>
 *     <li>{@link ExpireBytesPersistenceSolver}</li>
 *     <li>{@link ExpirePersistenceSolver}</li>
 * </ul>
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public interface Dispose {

    /**
     * Rear cache persistence operations
     *
     * @param solver   must not be {@literal null}
     * @param variable must not be {@literal null}
     */
    void dispose(@NotNull PersistenceSolver solver, @NotNull DisposeVariable variable);

    /**
     * Get enumerated type
     *
     * @return {@link PersistenceExecTypeEnum}
     */
    PersistenceExecTypeEnum getExecType();

    /**
     * Default Rear cache persistence operations
     *
     * @param solver must not be {@literal null}
     * @param args   can be {@literal null}
     */
    default void dispose(@NotNull PersistenceSolver solver, @CanNull Object[] args) {
        dispose(solver, convert(getExecType(), args));
    }

    /**
     * Cut parameter conversion method by default
     *
     * @param execTypeEnum The specified enumeration class
     * @param args         Need to transform the parameter set
     * @return Transformation parameters object
     */
    default DisposeVariable convert(PersistenceExecTypeEnum execTypeEnum, Object[] args) {
        DisposeVariable variable;
        switch (execTypeEnum) {
            case SET:
                variable = DisposeVariable.analysisSet(args);
                break;
            case REPLACE_VALUE:
                variable = DisposeVariable.analysisReplaceValue(args);
                break;
            case REPLACE_DURATION:
                variable = DisposeVariable.analysisReplaceDuration(args);
                break;
            case REST_DURATION:
                variable = DisposeVariable.analysisRestDuration(args);
                break;
            case REMOVE_KEYS:
                variable = DisposeVariable.analysisRemoveKeys(args);
                break;
            case REMOVE_TYPE:
                variable = DisposeVariable.analysisRemoveType(args);
                break;
            case REMOVE_ALL:
                variable = DisposeVariable.analysisRemoveAll(args);
                break;
            default:
                variable = DisposeVariable.init();
                break;
        }
        return variable;
    }

    class DisposeVariable implements Serializable {

        private static final long serialVersionUID = -1809461008323016041L;
        private Object key;
        private Object value;
        private Object newValue;
        private Long duration;
        private TimeUnit unit;
        private List<Object> anyKeys;

        public DisposeVariable() {
        }

        static int indexOne = 0;

        static int indexTwo = 1;

        static int indexThree = 2;

        static int indexFour = 3;

        static int lengthSi = 1;

        static int lengthSimple = 2;

        static int lengthGan = 3;

        static int lengthDlg = 4;

        private void setKey(Object key) {
            this.key = key;
        }

        private void setValue(Object value) {
            this.value = value;
        }

        private void setNewValue(Object newValue) {
            this.newValue = newValue;
        }

        private void setAnyKeys(List<Object> anyKeys) {
            this.anyKeys = anyKeys;
        }

        private void setDuration(Object duration) {
            this.duration = Long.parseLong(duration.toString());
        }

        private void setUnit(Object unit) {
            this.unit = TimeUnit.valueOf(unit.toString());
        }

        public Long getDuration() {
            return duration;
        }

        public List<Object> getAnyKeys() {
            return anyKeys;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object getNewValue() {
            return newValue;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        private static DisposeVariable init() {
            return new DisposeVariable();
        }

        /*
         * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object) or
         * @see io.github.zpf9705.expiring.core.ValueOperations#set(Object, Object, Long, TimeUnit)
         */
        private static DisposeVariable analysisSet(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthSimple) {
                variable.setKey(args[indexOne]);
                variable.setValue(args[indexTwo]);
            } else if (args.length == lengthDlg) {
                variable.setKey(args[indexOne]);
                variable.setValue(args[indexTwo]);
                variable.setDuration(args[indexThree]);
                variable.setUnit(args[indexFour]);
            }
            return variable;
        }

        /*
         * @see io.github.zpf9705.expiring.core.ValueOperations#getAndSet(Object, Object)
         */
        private static DisposeVariable analysisReplaceValue(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthSimple) {
                variable.setKey(args[indexOne]);
                variable.setNewValue(args[indexTwo]);
            }
            return variable;
        }

        /*
         * @see io.github.zpf9705.expiring.core.ExpirationOperations#setExpiration(Object, Long, TimeUnit)
         */
        private static DisposeVariable analysisReplaceDuration(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthGan) {
                variable.setKey(args[indexOne]);
                variable.setDuration(args[indexTwo]);
                variable.setUnit(args[indexThree]);
            }
            return variable;
        }

        /*
         * @see io.github.zpf9705.expiring.core.ExpirationOperations#resetExpiration(Object)
         */
        public static DisposeVariable analysisRestDuration(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthSi) {
                variable.setKey(args[indexOne]);
            }
            return variable;
        }

        /*
         * @see io.github.zpf9705.expiring.core.ExpireOperations#delete(Collection)
         */
        public static DisposeVariable analysisRemoveKeys(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthSi) {
                Object o = args[indexOne];
                if (o.getClass().isArray()) {
                    Object[] array = (Object[]) o;
                    variable.setAnyKeys(Arrays.asList(array));
                } else {
                    variable.setAnyKeys(Collections.singletonList(o));
                }
            }
            return variable;
        }

        /*
         * @see io.github.zpf9705.expiring.core.ExpireOperations#deleteType(Object)
         */
        private static DisposeVariable analysisRemoveType(@NotNull Object[] args) {
            DisposeVariable variable = init();
            if (args.length == lengthSi) {
                variable.setKey(args[indexOne]);
            }
            return variable;
        }

        /*
         * @see ExpireOperations#deleteAll()
         */
        private static DisposeVariable analysisRemoveAll(@CanNull Object[] args) {
            return args == null ? DisposeVariable.init() : null;
        }
    }
}
