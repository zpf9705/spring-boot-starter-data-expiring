package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.help.Reload;
import io.github.zpf9705.expiring.help.ReloadCarry;

import java.util.concurrent.TimeUnit;

/**
 * Reload for carry value for {@code ExpiringMap} {@link ExpireMapCenter}
 *
 * @author zpf
 * @since 3.0.0
 */
@Reload(client = "EXPIRE_MAP")
public class ExpireMapCenterReload implements ReloadCarry<byte[], byte[]> {

    @Override
    public void reload(@NotNull byte[] key, @NotNull byte[] value, @NotNull Long duration, @NotNull TimeUnit unit) {
        ExpireMapCenter expireMapCenter = ExpireMapCenter.getExpireMapCenter();
        if (expireMapCenter != null) {
            expireMapCenter.reload(key, value, duration, unit);
        }
    }
}
