package io.github.zpf9705.expiring.banner;

/**
 * Start up banner for {@link net.jodah.expiringmap.ExpiringMap}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapBanner implements StartUpBanner {

    private static final String BANNER = "" +
            " ████████                 ██                  ████     ████                  \n" +
            "░██░░░░░          ██████ ░░                  ░██░██   ██░██           ██████ \n" +
            "░██       ██   ██░██░░░██ ██ ██████  █████   ░██░░██ ██ ░██  ██████  ░██░░░██\n" +
            "░███████ ░░██ ██ ░██  ░██░██░░██░░█ ██░░░██  ░██ ░░███  ░██ ░░░░░░██ ░██  ░██\n" +
            "░██░░░░   ░░███  ░██████ ░██ ░██ ░ ░███████  ░██  ░░█   ░██  ███████ ░██████ \n" +
            "░██        ██░██ ░██░░░  ░██ ░██   ░██░░░░   ░██   ░    ░██ ██░░░░██ ░██░░░  \n" +
            "░████████ ██ ░░██░██     ░██░███   ░░██████  ░██        ░██░░████████░██     \n" +
            "░░░░░░░░ ░░   ░░ ░░      ░░ ░░░     ░░░░░░   ░░         ░░  ░░░░░░░░ ░░      ";


    private static final String EXPIRE_MAP_SINE = " :: Expire Map :: ";

    @Override
    public String getBanner() {
        return BANNER;
    }

    @Override
    public String getLeftSign() {
        return EXPIRE_MAP_SINE;
    }
}
