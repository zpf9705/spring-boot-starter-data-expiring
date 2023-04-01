package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.proxy.DefaultJdkProxyInvocationHandler;

/**
 * Cache operation follow-up persistent class content method of unified processing method
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpirePersistenceAfterHandle extends DefaultJdkProxyInvocationHandler {

    @Override
    public Class<PersistenceExec> getProxyAnnotation() {
        return super.getProxyAnnotation();
    }

    @Override
    public void invokeSubsequent(PersistenceExec proxyExec, Object[] args) {
        super.invokeSubsequent(proxyExec, args);
        //dispose proxyExec
        proxyExec.value().dispose(args);
    }
}
