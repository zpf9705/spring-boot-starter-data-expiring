package io.github.zpf9705.expiring.logger;


import org.slf4j.LoggerFactory;

/**
 * System log input class, using {@link org.slf4j.Logger}
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class Console {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Console.class);

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

    public static class DefaultConsole implements Logger {

        public static DefaultConsole me(){
            return new DefaultConsole();
        }

        @Override
        public void info(String format, Object... arguments) {
            Console.info(format, arguments);
        }

        @Override
        public void info(String msg, Throwable e) {
            Console.info(msg, e);
        }

        @Override
        public void warn(String format, Object... arguments) {
            Console.warn(format, arguments);
        }

        @Override
        public void warn(String msg, Throwable e) {
            Console.warn(msg, e);
        }

        @Override
        public void trace(String format, Object... arguments) {
            Console.trace(format, arguments);
        }

        @Override
        public void trace(String msg, Throwable e) {
            Console.trace(msg, e);
        }

        @Override
        public void debug(String format, Object... arguments) {
            Console.debug(format, arguments);
        }

        @Override
        public void debug(String msg, Throwable e) {
            Console.debug(msg, e);
        }

        @Override
        public void error(String format, Object... arguments) {
            Console.error(format, arguments);
        }

        @Override
        public void error(String msg, Throwable e) {
            Console.error(msg, e);
        }
    }
}
