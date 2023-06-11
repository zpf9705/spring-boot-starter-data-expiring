package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapByteContain;
import io.github.zpf9705.expiring.util.CodecUtils;
import net.jodah.expiringmap.ExpiringMap;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Abstract expiry helper classes , Link the abstract template for {@link ExpireHelper}
 * Provide some check on expiry the implementer, tips and some other help category .
 * <p>
 * To provide an interface can extend the cache class
 * And global unified configuration byte [] types of cache model
 * Can be by a specific method to obtain the corresponding help center
 * <ul>
 *     <li>{@link HelpCenter}</li>
 *     <li>{@link HelpCenter#getHelpCenter()}</li>
 *     <li>{@link HelpCenter#getContain()}</li>
 * </ul>
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbstractExpireHelper<T> implements DefaultedExpireHelper {

    private final HelpCenter<T> helpCenter;

    public AbstractExpireHelper(@NotNull HelpCenter<T> helpCenter) {
        this.helpCenter = helpCenter;
    }

    /**
     * Get a The cache helper for {@link HelpCenter}
     *
     * @return {@link HelpCenter}
     */
    public T getHelpCenter() {
        return this.helpCenter.getHelpCenter();
    }

    /**
     * Get {@link ExpireMapByteContain} operation adapter
     *
     * @return {@link ExpiringMap}
     */
    public ExpireMapByteContain contain() {
        return this.helpCenter.getContain();
    }

    /**
     * Similar object {@code key} and object {@code value}  check function expression
     */
    private final BiFunction<Object, Object, Boolean> compare = (b, c) -> {
        if (b != null && c != null) {
            Predicate<String> predicate = CodecUtils.findPredicate(
                    CodecUtils.toStingBeReal(c)
            );
            return predicate.test(CodecUtils.toStingBeReal(b));
        }
        return false;
    };

    /**
     * Compare with {@code byte[]} of similar
     *
     * @param compare  must not be {@literal null}.
     * @param compare_ must not be {@literal null}.
     * @return if {@literal true} prove that similar
     */
    public boolean similarJudgeOfBytes(byte[] compare, byte[] compare_) {
        return this.compare.apply(compare, compare_);
    }
}
