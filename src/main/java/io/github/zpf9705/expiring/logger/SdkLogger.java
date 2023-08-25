package io.github.zpf9705.expiring.logger;


/**
 * A class specifically used for printing input logs for SDK example components.
 *
 * @author zpf
 * @since 3.2.1
 */
public abstract class SdkLogger {

    private static Logger logger;

    public static void loadClass(Class<? extends Logger> clazz) {
        logger = Logger.load(clazz);
    }

    public static void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public static void info(String msg, Throwable e) {
        logger.info(msg, e);
    }

    public static void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    public static void warn(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    public static void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    public static void trace(String msg, Throwable e) {
        logger.trace(msg, e);
    }

    public static void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public static void debug(String msg, Throwable e) {
        logger.debug(msg, e);
    }

    public static void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public static void error(String msg, Throwable e) {
        logger.error(msg, e);
    }
}
