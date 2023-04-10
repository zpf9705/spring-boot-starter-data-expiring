package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.util.ServiceLoaderUtil;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionProxy;
import io.github.zpf9705.expiring.core.logger.Console;
import io.github.zpf9705.expiring.core.proxy.JdkProxyInvocationTrace;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

import java.util.ServiceLoader;

/**
 * Cache operation follow-up persistent class content method of unified processing method
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpirePersistenceAfterHandle extends JdkProxyInvocationTrace<ExpireMapConnectionProxy, PersistenceExec> {

    private static final long serialVersionUID = -2346911415600201852L;

    public ExpirePersistenceAfterHandle(ExpireMapConnectionProxy target, Class<PersistenceExec> annotationClass) {
        super(target, annotationClass);
    }

    @Override
    public Class<PersistenceExec> getProxyAnnotation() {
        return PersistenceExec.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void invokeSubsequent(PersistenceExec proxyExec, Object[] args) {
        super.invokeSubsequent(proxyExec, args);
        //dispose proxyExec
        PersistenceSolver solver = ServiceLoadUtils.load(PersistenceSolver.class)
                .getSpecifiedServiceBySubClass(proxyExec.shouldSolver());
        if (solver == null) {
            Console.warn("Provider Persistence [{}] shouldSolver load null", proxyExec.shouldSolver().getName());
            return;
        }
        proxyExec.value().dispose(solver, args);
    }
}
