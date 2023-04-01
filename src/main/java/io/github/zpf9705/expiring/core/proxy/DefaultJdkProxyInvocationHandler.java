package io.github.zpf9705.expiring.core.proxy;

import io.github.zpf9705.expiring.core.PersistenceExec;
import io.github.zpf9705.expiring.util.AssertUtils;

/**
 * <p>
 *
 * <p>
 *
 * @author zpf
 * @since 3.0.0
 */
public class DefaultJdkProxyInvocationHandler extends JdkProxyInvocationHandler<PersistenceExec> {

    @Override
    public Class<PersistenceExec> getProxyAnnotation() {
        return PersistenceExec.class;
    }

    @Override
    public void invokeSubsequent(PersistenceExec proxyExec, Object[] args) {
        AssertUtils.Persistence.notNull(proxyExec, "PersistenceExec no be null");
        AssertUtils.Persistence.notEmpty(args, "Args no be null");
    }
}
