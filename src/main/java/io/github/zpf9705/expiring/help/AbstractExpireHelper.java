package io.github.zpf9705.expiring.help;

import io.github.zpf9705.expiring.util.ObjectUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Link the abstract template for {@link ExpireHelper}
 *
 * @author zpf
 * @since 3.0.0
 */
public abstract class AbstractExpireHelper implements DefaultedExpireHelper {

    /**
     * Similar object key and object value  check function expression
     */
    private final BiFunction<Object, Object, Boolean> compare = (b, c) -> {
        if (b != null && c != null) {
            List<String> source =
                    ObjectUtils.changeListWithComma(ObjectUtils.toStingWithMiddle(c));
            Predicate<String> predicate = ObjectUtils.findPredicate(source);
            return predicate.test(ObjectUtils.toStingWithMiddle(c));
        }
        return false;
    };

    /**
     * Compare with byte[] of similar
     *
     * @param compare  must not be {@literal null}.
     * @param compare_ must not be {@literal null}.
     * @return if {@literal true} prove that similar
     */
    public boolean SimilarJudgeOfBytes(byte[] compare, byte[] compare_) {
        return this.compare.apply(compare, compare_);
    }
}
