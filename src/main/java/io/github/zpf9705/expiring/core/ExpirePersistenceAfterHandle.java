package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnection;
import io.github.zpf9705.expiring.core.proxy.JdkProxyInvocationTrace;

/**
 * Cache operation follow-up persistent class content method of unified processing method
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpirePersistenceAfterHandle extends JdkProxyInvocationTrace<ExpireMapConnection, PersistenceExec> {

    private static final long serialVersionUID = -2346911415600201852L;

    public ExpirePersistenceAfterHandle(ExpireMapConnection target, Class<PersistenceExec> annotationClass) {
        super(target, annotationClass);
    }

    @Override
    public Class<PersistenceExec> getProxyAnnotation() {
        return PersistenceExec.class;
    }

    @Override
    public void invokeSubsequent(PersistenceExec proxyExec, Object[] args) {
        super.invokeSubsequent(proxyExec, args);
        //dispose proxyExec
        proxyExec.value().dispose(args);
    }
}
