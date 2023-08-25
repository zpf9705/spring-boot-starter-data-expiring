package io.github.zpf9705.expiring.autoconfigure;

/**
 * Print version information and custom log information about {@code Spring-boot-starter-data-expiring}.
 * Copy the method to {@code org.springframework.boot.SpringBootBanner}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireStarterBanner implements StartUpBanner {

    private static final String BANNER = "" +
            " ██                          ██                             ██               \n" +
            "░██                         ░██                     ██████ ░░                \n" +
            "░██       ██████   ██████  ██████    █████  ██   ██░██░░░██ ██ ██████  █████ \n" +
            "░██████  ██░░░░██ ██░░░░██░░░██░    ██░░░██░░██ ██ ░██  ░██░██░░██░░█ ██░░░██\n" +
            "░██░░░██░██   ░██░██   ░██  ░██    ░███████ ░░███  ░██████ ░██ ░██ ░ ░███████\n" +
            "░██  ░██░██   ░██░██   ░██  ░██    ░██░░░░   ██░██ ░██░░░  ░██ ░██   ░██░░░░ \n" +
            "░██████ ░░██████ ░░██████   ░░██   ░░██████ ██ ░░██░██     ░██░███   ░░██████\n" +
            "░░░░░    ░░░░░░   ░░░░░░     ░░     ░░░░░░ ░░   ░░ ░░      ░░ ░░░     ░░░░░░ ";


    private static final String EXPIRE_STARTERS_SINE = " :: Spring Boot Expire :: ";

    @Override
    public String getBanner() {
        return BANNER;
    }

    @Override
    public String getLeftSign() {
        return EXPIRE_STARTERS_SINE;
    }
}
