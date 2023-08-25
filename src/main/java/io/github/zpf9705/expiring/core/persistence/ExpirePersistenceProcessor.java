package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.ExpiryInvocationTrace;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.ServiceLoadUtils;

/**
 * Proxy object operation side method execution for persistent cache files.
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public class ExpirePersistenceProcessor<H> extends ExpiryInvocationTrace<H, PersistenceExec> {

    private static final long serialVersionUID = -2346911415600201852L;

    public ExpirePersistenceProcessor(H target) {
        super(target, PersistenceExec.class);
    }

    @Override
    @NotNull
    public Class<PersistenceExec> getAppointAnnotationClazz() {
        return PersistenceExec.class;
    }

    @Override
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
