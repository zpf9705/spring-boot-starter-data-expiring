/**
 * @see net.jodah.expiringmap.ExpiringMap
 *               ||
 *               ||
 *               ||
 * --------------------Cache the template enclosed-----------------------------------
 * @see io.github.expiring.core.ExpireTemplate
 * @see io.github.expiring.core.StringExpiredTemplate
 * @see io.github.expiring.core.ExpireOperations
 * @see io.github.expiring.core.ValueOperations
 * @see io.github.expiring.core.DefaultValueOperations
 * @see io.github.expiring.core.AbstractExpireAccessor
 *
 * ========================Overdue listener===================================
 * {@link io.github.expiring.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link io.github.expiring.core.ExpiringListeners#value()} Radio listening
 * -
 * -
 * =======================The persistent cache data to the hard disk============================
 * @see io.github.expiring.core.ExpireMapCacheProperties#getPersistence() getOpenPersistence() --- Whether to open the persistence
 * @see io.github.expiring.core.ExpireMapCacheProperties#getPersistence() getPersistencePath() () -- Persistent path
 * @see io.github.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTime() () -- Persistence time
 * @see io.github.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTimeUnit() () () -- Provisions of the unit
 * @see io.github.expiring.core.AbstractGlobePersistenceIndicator Define persistent behavior
 * @see io.github.expiring.core.ExpireGlobePersistence The persistence method
 * @see io.github.expiring.core.GlobePersistence Define the persistence
 * @see io.github.expiring.core.PersistenceExpiringCallback After listening to perform rear remove persistent file
 */
package io.github.expiring.core;

