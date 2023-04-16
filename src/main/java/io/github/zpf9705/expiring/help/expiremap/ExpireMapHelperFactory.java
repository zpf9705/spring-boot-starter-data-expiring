package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.persistence.ExpirePersistenceAfterHandle;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.proxy.JdkProxy;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

/**
 * Connection factory creating {@link ExpireMapHelperFactory}
 * with {@link ExpireMapClientConfiguration}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapHelperFactory implements ExpireHelperFactory {

    private final ExpireMapClientConfiguration clientConfiguration;

    private final ExpireMapHelperProxy proxy;

    public ExpireMapHelperFactory(@NonNull ExpireMapClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        this.proxy = doCreateExpireMapConnection(this.clientConfiguration);
    }

    @Override
    @NonNull
    public ExpireHelper getHelper() {
        return this.proxy;
    }

    /**
     * Setting a Expire Map connection
     *
     * @param clientConfiguration {@link ExpireMapClientConfiguration}
     * @return return a {@link ExpireMapHelper}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ExpireMapHelperProxy doCreateExpireMapConnection(ExpireMapClientConfiguration clientConfiguration) {
        ExpiringMap<byte[], byte[]> expiringMap = ExpiringMap.builder()
                .maxSize(this.clientConfiguration.getMaxSize())
                .expiration(this.clientConfiguration.getDefaultExpireTime(),
                        this.clientConfiguration.getDefaultExpireTimeUnit())
                .expirationPolicy(this.clientConfiguration.getExpirationPolicy())
                .variableExpiration()
                .build();
        if (!CollectionUtils.isEmpty(clientConfiguration.getSyncExpirationListeners())) {
            for (ExpirationListener expirationListener : clientConfiguration.getSyncExpirationListeners()) {
                //sync
                expiringMap.addExpirationListener(expirationListener);

            }
        }
        if (!CollectionUtils.isEmpty(clientConfiguration.getASyncExpirationListeners())){
            for (ExpirationListener expirationListener : clientConfiguration.getASyncExpirationListeners()) {
                //async
                expiringMap.addAsyncExpirationListener(expirationListener);

            }
        }
        return JdkProxy.createProxy(
                new ExpirePersistenceAfterHandle(new ExpireMapHelper(expiringMap), PersistenceExec.class));
    }
}
