package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.expiring.core.ExpireOperations;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The cache persistence operation interface for {@link PersistenceSolver}
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public interface Dispose {

    /**
     * Rear cache persistence operations
     *
     * @param solver must not be {@literal null}
     * @param args   can  be {@literal null}
     */
    void dispose(@NotNull PersistenceSolver solver, @CanNull Object[] args);

    @NoArgsConstructor
    class DisposeVariable implements Serializable {

        private static final long serialVersionUID = -1809461008323016041L;

        @Getter
        private Object key;
        @Getter
        private Object value;
        @Getter
        private Object newValue;
        @Getter
        private Long duration;
        @Getter
        private TimeUnit unit;
        @Getter
        private List<Object> anyKeys;

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
            return args == null ? null : DisposeVariable.init();
        }
    }

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

    int indexOne = 0;
    int indexTwo = 1;
    int indexThree = 2;
    int indexFour = 3;
    int lengthSi = 1;
    int lengthSimple = 2;
    int lengthGan = 3;
    int lengthDlg = 4;
}
