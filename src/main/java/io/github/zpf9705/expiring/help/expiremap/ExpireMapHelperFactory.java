package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.persistence.ExpirePersistenceAfterHandle;
import io.github.zpf9705.expiring.core.persistence.PersistenceExec;
import io.github.zpf9705.expiring.core.proxy.JdkProxy;
import io.github.zpf9705.expiring.help.ExpireHelper;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;

/**
 * Connection factory creating {@link ExpireMapHelperFactory}
 * with {@link ExpireMapClientConfiguration}
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
        ExpirePersistenceAfterHandle afterHandle = new ExpirePersistenceAfterHandle(
                new ExpireMapRealHelper(
                        () -> ExpireMapCenter.singletonWithConfiguration(clientConfiguration)
                ), PersistenceExec.class
        );
        return JdkProxy.createProxy(afterHandle);
    }
}
