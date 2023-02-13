/**
 * @see net.jodah.expiringmap.ExpiringMap
 *               ||
 *               ||
 *               ||
 * --------------------Cache the template enclosed-----------------------------------
 * @see io.github.zpf9705.core.ExpireTemplate
 * @see io.github.zpf9705.core.StringExpiredTemplate
 * @see io.github.zpf9705.core.ExpireOperations
 * @see io.github.zpf9705.core.ValueOperations
 * @see io.github.zpf9705.core.DefaultValueOperations
 * @see io.github.zpf9705.core.AbstractExpireAccessor
 *
 * ========================Overdue listener===================================
 * {@link io.github.zpf9705.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link io.github.zpf9705.core.ExpiringListeners#value()} Radio listening
 * -
 * -
 * =======================The persistent cache data to the hard disk============================
 * @see io.github.zpf9705.core.ExpireMapCacheProperties#getPersistence() getOpenPersistence() --- Whether to open the persistence
 * @see io.github.zpf9705.core.ExpireMapCacheProperties#getPersistence() getPersistencePath() () -- Persistent path
 * @see io.github.zpf9705.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTime() () -- Persistence time
 * @see io.github.zpf9705.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTimeUnit() () () -- Provisions of the unit
 * @see io.github.zpf9705.core.AbstractGlobePersistenceIndicator Define persistent behavior
 * @see io.github.zpf9705.core.ExpireGlobePersistence The persistence method
 * @see io.github.zpf9705.core.GlobePersistence Define the persistence
 * @see io.github.zpf9705.core.PersistenceExpiringCallback After listening to perform rear remove persistent file
 */
package io.github.zpf9705.core;

