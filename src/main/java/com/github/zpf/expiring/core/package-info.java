/**
 * @see net.jodah.expiringmap.ExpiringMap
 *               ||
 *               ||
 *               ||
 * --------------------Cache the template enclosed-----------------------------------
 * @see com.github.zpf.expiring.core.ExpireTemplate
 * @see com.github.zpf.expiring.core.StringExpiredTemplate
 * @see com.github.zpf.expiring.core.ExpireOperations
 * @see com.github.zpf.expiring.core.ValueOperations
 * @see com.github.zpf.expiring.core.DefaultValueOperations
 * @see com.github.zpf.expiring.core.AbstractExpireAccessor
 *
 * ========================Overdue listener===================================
 * {@link com.github.zpf.expiring.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link com.github.zpf.expiring.core.ExpiringListeners#value()} Radio listening
 * -
 * -
 * =======================The persistent cache data to the hard disk============================
 * @see com.github.zpf.expiring.core.ExpireMapCacheProperties#getPersistence() getOpenPersistence() --- Whether to open the persistence
 * @see com.github.zpf.expiring.core.ExpireMapCacheProperties#getPersistence() getPersistencePath() () -- Persistent path
 * @see com.github.zpf.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTime() () -- Persistence time
 * @see com.github.zpf.expiring.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTimeUnit() () () -- Provisions of the unit
 * @see com.github.zpf.expiring.core.AbstractGlobePersistenceIndicator Define persistent behavior
 * @see com.github.zpf.expiring.core.ExpireGlobePersistence The persistence method
 * @see com.github.zpf.expiring.core.GlobePersistence Define the persistence
 * @see com.github.zpf.expiring.core.PersistenceExpiringCallback After listening to perform rear remove persistent file
 */
package com.github.zpf.expiring.core;

