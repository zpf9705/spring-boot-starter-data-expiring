/**
 * @see net.jodah.expiringmap.ExpiringMap
 *               ||
 *               ||
 *               ||
 * --------------------Cache the template enclosed-----------------------------------
 * @see com.bookuu.soft.ec.core.ExpireTemplate
 * @see com.bookuu.soft.ec.core.StringExpiredTemplate
 * @see com.bookuu.soft.ec.core.ExpireOperations
 * @see com.bookuu.soft.ec.core.ValueOperations
 * @see com.bookuu.soft.ec.core.DefaultValueOperations
 * @see com.bookuu.soft.ec.core.AbstractExpireAccessor
 *
 * ========================Overdue listener===================================
 * {@link com.bookuu.soft.ec.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link com.bookuu.soft.ec.core.ExpiringListeners#value()} Radio listening
 * -
 * -
 * =======================The persistent cache data to the hard disk============================
 * @see com.bookuu.soft.ec.core.ExpireMapCacheProperties#getPersistence() getOpenPersistence() --- Whether to open the persistence
 * @see com.bookuu.soft.ec.core.ExpireMapCacheProperties#getPersistence() getPersistencePath() () -- Persistent path
 * @see com.bookuu.soft.ec.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTime() () -- Persistence time
 * @see com.bookuu.soft.ec.core.ExpireMapCacheProperties#getPersistence() getNoPersistenceOfExpireTimeUnit() () () -- Provisions of the unit
 * @see com.bookuu.soft.ec.core.AbstractGlobePersistenceIndicator Define persistent behavior
 * @see com.bookuu.soft.ec.core.ExpireGlobePersistence The persistence method
 * @see com.bookuu.soft.ec.core.GlobePersistence Define the persistence
 * @see com.bookuu.soft.ec.core.PersistenceExpiringCallback After listening to perform rear remove persistent file
 */
package com.bookuu.soft.ec.core;

