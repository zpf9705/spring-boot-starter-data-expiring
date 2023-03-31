package io.github.zpf9705.expiring.connection.expiremap;

import io.github.zpf9705.expiring.connection.ExpireConnection;
import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

/**
 * Connection factory creating {@link ExpireMapConnectionFactory} with {@link ExpireMapClientConfiguration}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapConnectionFactory implements ExpireConnectionFactory {

    private final ExpireMapClientConfiguration clientConfiguration;

    public ExpireMapConnectionFactory(@NonNull ExpireMapClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    @NonNull
    public ExpireConnection getConnection() {
        return buildExpireMapConnection(this.clientConfiguration);
    }

    /**
     * Setting a Expire Map connection
     *
     * @param clientConfiguration {@link ExpireMapClientConfiguration}
     * @return return a {@link ExpireMapConnection}
     */
    public ExpireMapConnection buildExpireMapConnection(ExpireMapClientConfiguration clientConfiguration) {
        ExpiringMap<byte[], byte[]> expiringMap = ExpiringMap.builder()
                .maxSize(this.clientConfiguration.getMaxSize())
                .expiration(this.clientConfiguration.getDefaultExpireTime(),
                        this.clientConfiguration.getDefaultExpireTimeUnit())
                .expirationPolicy(this.clientConfiguration.getExpirationPolicy())
                .variableExpiration()
                .build();
        if (!CollectionUtils.isEmpty(clientConfiguration.getExpirationListeners())) {
            for (ExpirationListener expirationListener : clientConfiguration.getExpirationListeners()) {
                expiringMap.addExpirationListener(expirationListener);
            }
        }
        return new ExpireMapConnection(expiringMap);
    }
}
