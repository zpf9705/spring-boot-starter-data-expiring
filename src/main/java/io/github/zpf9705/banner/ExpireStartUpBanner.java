package io.github.zpf9705.banner;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;
import java.io.PrintStream;

/**
 * Print the expire - map banners and version information {@link Banner}
 *
 * @author zpf
 * @since 2.2.2
 */
public class ExpireStartUpBanner implements Banner {

    private static final String BANNER = "" +
            " ████████                 ██                  ████     ████                  \n" +
            "░██░░░░░          ██████ ░░                  ░██░██   ██░██           ██████ \n" +
            "░██       ██   ██░██░░░██ ██ ██████  █████   ░██░░██ ██ ░██  ██████  ░██░░░██\n" +
            "░███████ ░░██ ██ ░██  ░██░██░░██░░█ ██░░░██  ░██ ░░███  ░██ ░░░░░░██ ░██  ░██\n" +
            "░██░░░░   ░░███  ░██████ ░██ ░██ ░ ░███████  ░██  ░░█   ░██  ███████ ░██████ \n" +
            "░██        ██░██ ░██░░░  ░██ ░██   ░██░░░░   ░██   ░    ░██ ██░░░░██ ░██░░░  \n" +
            "░████████ ██ ░░██░██     ░██░███   ░░██████  ░██        ░██░░████████░██     \n" +
            "░░░░░░░░ ░░   ░░ ░░      ░░ ░░░     ░░░░░░   ░░         ░░  ░░░░░░░░ ░░      ";

    private static final String EXPIRE_MAP = " :: Expire Map :: ";

    private static final int STRAP_LINE_SIZE = 42;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        printStream.println(BANNER);
        String version = ExpireMapVersion.getVersion();
        version = (version != null) ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + EXPIRE_MAP.length())) {
            padding.append(" ");
        }
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, EXPIRE_MAP, AnsiColor.DEFAULT, padding.toString(),
                AnsiStyle.FAINT, version));
        printStream.println();
    }

    private ExpireStartUpBanner() {
    }

    static class ExpireStartBootBannerPrinter {
        static final ExpireStartUpBanner defaultExpireStartUpBanner = new ExpireStartUpBanner();
    }

    public static void bannerPrinter(Environment environment, Class<?> sourceClass) {
        ExpireStartBootBannerPrinter.defaultExpireStartUpBanner.printBanner(environment, sourceClass, System.out);
    }
}
