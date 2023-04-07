package io.github.zpf9705.expiring.connection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.SerializationUtils;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Link the abstract template for {@link ExpireConnection}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbstractExpireConnection implements DefaultedExpireConnection {

    /**
     * Similar key check function expression
     */
    protected static final BiPredicate<String, String> type_predicate = (serial, simpler) -> {
        if (StringUtils.isBlank(serial)) {
            return false;
        }
        return serial.equals(simpler) ||
                serial.startsWith(simpler) ||
                serial.endsWith(simpler) ||
                serial.contains(simpler);
    };

    /**
     * Similar key deserialize check function expression
     */
    protected static final BiFunction<byte[], byte[], Boolean> deserialize = (b, c) -> {
        Object de = SerializationUtils.deserialize(b);
        Object ce = SerializationUtils.deserialize(c);
        if (de != null && ce != null) {
            return type_predicate.test(de.toString(), ce.toString());
        }
        return false;
    };
}
