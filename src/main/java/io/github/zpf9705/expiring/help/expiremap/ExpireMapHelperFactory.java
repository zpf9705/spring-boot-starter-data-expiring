package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.persistence.ExpirePersistenceProcessor;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.proxy.JdkProxy;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;

/**
 * ExpireMap Connection factory creating for {@code ExpireHelperFactory}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapHelperFactory implements ExpireHelperFactory {

    private final ExpireMapHelper helper;

    public ExpireMapHelperFactory(@NotNull ExpireMapClientConfiguration clientConfiguration) {
        this.helper = doCreateExpireMapHelp(clientConfiguration);
    }

    @Override
    @NotNull
    public ExpireHelper getHelper() {
        return this.helper;
    }

    /**
     * Setting a Expire Map connection
     *
     * @param clientConfiguration {@link ExpireMapClientConfiguration}
     * @return return a {@link ExpireMapHelper}
     */
    public ExpireMapHelper doCreateExpireMapHelp(ExpireMapClientConfiguration clientConfiguration) {
        //Real object generated singleton operation
        ExpireMapCenter expireMapCenter = ExpireMapCenter.singletonWithConfiguration(clientConfiguration);
        //To approach the processor
        ExpireMapPersistenceProcessor processor = ExpireMapPersistenceProcessor.buildProcessor(
                new ExpireMapRealHelper(() -> expireMapCenter)
        );
        return JdkProxy.createProxy(processor);
    }
}
