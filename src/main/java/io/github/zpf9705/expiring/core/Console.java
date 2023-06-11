package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use date records for this item
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class Console {

    private static Logger logger = LoggerFactory.getLogger(Console.class);

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    public static void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    public static void info(String msg, Throwable e) {
        logger.info(msg, e);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    public static void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    public static void warn(String msg, Throwable e) {
        logger.warn(msg, e);
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    public static void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    public static void trace(String msg, Throwable e) {
        logger.trace(msg, e);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    public static void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    public static void debug(String msg, Throwable e) {
        logger.debug(msg, e);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    public static void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    public static void error(String msg, Throwable e) {
        logger.error(msg, e);
    }


    /**
     * Print warn or debug information
     *
     * @param target logger target
     * @param ex     exception for it
     * @param format format statement
     * @since 2.1.2-complete
     */
    @Deprecated
    public static void exceptionOfDebugOrWare(String target, Throwable ex, String format) {
        if (StringUtils.simpleIsBlank(target) || ex == null) {
            return;
        }
        boolean print = false;
        if (logger.isDebugEnabled()) {
            debug(format, target, ex.getMessage());
            print = true;
        } else if (logger.isWarnEnabled()) {
            warn(format, target, ex.getMessage());
            print = true;
        }
        //final no debug or warn enable print info logger
        if (!print) {
            info(format, target, ex.getMessage());
        }
    }

    @Deprecated
    public static Class<?> getThisClassGlobeLock() {
        return Console.class;
    }

    /**
     * Access to the log print
     *
     * @return logger for slf4j
     * @since 2.1.2-complete
     */
    @Deprecated
    public static Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        synchronized (getThisClassGlobeLock()) {
            if (logger == null) {
                logger = LoggerFactory.getLogger(Console.class);
            }
        }
        return logger;
    }
}
