package io.github.zpf9705.expiring.autoconfigure;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * Logo printing practitioner
 *
 * @author zpf
 * @since 2.2.2
 */
public abstract class ExpireStartUpBannerExecutor {

    private static final int STRAP_LINE_SIZE = 42;

    /**
     * Print the banner to the specified print stream.
     *
     * @param environment spring environment
     * @param banner      startup banner for using
     * @param sourceClass using source class
     * @param printStream print type
     */
    public static void printBanner(@SuppressWarnings("unused") Environment environment,
                                   StartUpBanner banner, Class<?> sourceClass,
                                   PrintStream printStream) {
        printStream.println(banner.getBanner());
        String version = Version.getVersion(sourceClass);
        version = (version != null) ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + banner.getLeftSign().length())) {
            padding.append(" ");
        }
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, banner.getLeftSign(), AnsiColor.DEFAULT, padding.toString(),
                AnsiStyle.FAINT, version));
        printStream.println();
    }
}
