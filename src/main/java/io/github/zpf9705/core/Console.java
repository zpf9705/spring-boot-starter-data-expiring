package io.github.zpf9705.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use date records for this item
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class Console {

    public static Logger logger;

    /**
     * Access to the log print
     *
     * @return logger for slf4j
     * @since 2.1.2-complete
     */
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

    /**
     * Print warn or debug information
     *
     * @param target logger target
     * @param ex     exception for it
     * @since 2.1.2-complete
     */
    public static void exceptionOfDebugOrWare(String target, Throwable ex, String format) {
        if (StringUtils.isBlank(target) || ex == null) {
            logger.info("debugOrWare no provider info");
            return;
        }
        boolean print = false;
        Logger log = Console.logger;
        if (log.isDebugEnabled()) {
            log.debug(format, target, ex.getMessage());
            print = true;
        } else if (log.isWarnEnabled()) {
            log.warn(format, target, ex.getMessage());
            print = true;
        }
        //final no debug or warn enable print info logger
        if (!print) {
            log.info(format, target, ex.getMessage());
        }
    }

    public static Class<?> getThisClassGlobeLock() {
        return Console.class;
    }
}
