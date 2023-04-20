package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.core.proxy.JdkProxyInvocationTrace;
import io.github.zpf9705.expiring.help.expiremap.ExpireMapHelper;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

/**
 * Cache operation follow-up persistent class content method of unified processing method
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpirePersistenceAfterHandle extends JdkProxyInvocationTrace<ExpireMapHelper, PersistenceExec> {

    private static final long serialVersionUID = -2346911415600201852L;

    public ExpirePersistenceAfterHandle(ExpireMapHelper target, Class<PersistenceExec> annotationClass) {
        super(target, annotationClass);
    }

    @Override
    @NotNull
    public Class<PersistenceExec> getProxyAnnotation() {
        return PersistenceExec.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void invokeSubsequent(Object invokeResult, PersistenceExec proxyExec, Object[] args) {
        super.invokeSubsequent(invokeResult, proxyExec, args);
        //Did the test execution results in line with expectations
        if (!proxyExec.expectValue().test(invokeResult)) {
            return;
        }
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
