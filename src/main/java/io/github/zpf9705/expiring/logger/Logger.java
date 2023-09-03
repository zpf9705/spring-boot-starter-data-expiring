package io.github.zpf9705.expiring.logger;

import io.github.zpf9705.expiring.util.UtilsException;

/**
 * Define the log output method interface and provide it to the project as needed.
 * <p>
 * If not provided, the default is {@link Console}.
 *
 * @author zpf
 * @since 3.2.1
 */
public interface Logger {

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     */
    void info(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    void info(String msg, Throwable e);

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    void warn(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    void warn(String msg, Throwable e);

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    void trace(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the TRACE level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    void trace(String msg, Throwable e);

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    void debug(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    void debug(String msg, Throwable e);

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     * @param format    the format string
     * @param arguments a list of arguments
     * @since 2.3.0
     */
    void error(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param e   the exception (throwable) to log
     * @since 2.3.0
     */
    void error(String msg, Throwable e);

    /**
     * Instantiate the log {@link Logger} entity based on the class object
     *
     * @param clazz clazz object , if null default to {@link io.github.zpf9705.expiring.logger.Console.DefaultConsole}
     * @return must no be {@literal null}
     */
    static Logger load(Class<? extends Logger> clazz) {
        Logger logger;
        if (clazz == null) {
            logger = Console.DefaultConsole.self();
        } else {
            try {
                logger = clazz.newInstance();
            } catch (Exception e) {
                throw new UtilsException("Provider " + clazz.getName() + " Empty construct instantiation error : " +
                        e.getMessage());
            }
        }
        return logger;
    }
}
