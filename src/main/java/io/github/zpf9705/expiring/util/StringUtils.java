package io.github.zpf9705.expiring.util;

/**
 * @author zpf
 * @since 3.0.0
 */
public abstract class StringUtils {

    static final String EMPTY = "";

    /**
     * Is blank for {@code String}
     *
     * @param s Check String
     * @return if {@code true} Assertions by
     */
    public static boolean simpleIsBlank(final String s) {
        return !simpleNotBlank(s);
    }

    /**
     * Is not blank for {@code String}
     *
     * @param s Check String
     * @return if {@code true} Assertions by
     */
    public static boolean simpleNotBlank(final String s) {
        return null != s && !EMPTY.equals(s);
    }
}
