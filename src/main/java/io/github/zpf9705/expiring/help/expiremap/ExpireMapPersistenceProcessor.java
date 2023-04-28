package io.github.zpf9705.expiring.help.expiremap;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.persistence.ExpirePersistenceProcessor;

/**
 * {@link net.jodah.expiringmap.ExpiringMap} real target for {@link ExpirePersistenceProcessor}
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapPersistenceProcessor extends ExpirePersistenceProcessor<ExpireMapHelper> {

    private static final long serialVersionUID = 2955057534102013205L;

    public ExpireMapPersistenceProcessor(ExpireMapHelper target) {
        super(target);
    }

    /**
     * The static create methods for {@link ExpireMapHelper}
     *
     * @param helper {@link ExpireMapHelper}
     * @return {@link ExpireMapPersistenceProcessor}
     */
    public static ExpireMapPersistenceProcessor buildProcessor(@NotNull ExpireMapHelper helper) {
        return new ExpireMapPersistenceProcessor(helper);
    }
}
