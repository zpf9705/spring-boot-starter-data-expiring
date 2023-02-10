/**
 * @see net.jodah.expiringmap.ExpiringMap
 *               ||
 *               ||
 *               ||
 * --------------------Cache the template enclosed-----------------------------------
 * @see io.github.zpf1997.expiring.core.ExpireTemplate
 * @see io.github.zpf1997.expiring.core.StringExpiredTemplate
 * @see io.github.zpf1997.expiring.core.ExpireOperations
 * @see io.github.zpf1997.expiring.core.ValueOperations
 * @see io.github.zpf1997.expiring.core.DefaultValueOperations
 * @see io.github.zpf1997.expiring.core.AbstractExpireAccessor
 *
 * ========================Overdue listener===================================
 * {@link io.github.zpf1997.expiring.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link io.github.zpf1997.expiring.core.ExpiringListeners#value()} Radio listening
 * -
 * -
 * =======================The persistent cache data to the hard disk============================
 * @see io.github.zpf1997.expiring.core.ExpireMapCacheProperties#getPersistence() getOpenPersistence() --- Whether to open the persistence
 * @see io.github.zpf1997.expiring.core.ExpireMapCacheProperties#getPersistence() getPersistencePath() () -- Persistent path
 * @see io.github.zpf1997.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTime() () -- Persistence time
 * @see io.github.zpf1997.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTimeUnit() () () -- Provisions of the unit
 * @see io.github.zpf1997.expiring.core.AbstractGlobePersistenceIndicator Define persistent behavior
 * @see io.github.zpf1997.expiring.core.ExpireGlobePersistence The persistence method
 * @see io.github.zpf1997.expiring.core.GlobePersistence Define the persistence
 * @see io.github.zpf1997.expiring.core.PersistenceExpiringCallback After listening to perform rear remove persistent file
 */
package io.github.zpf1997.expiring.core;

