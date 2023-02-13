/**
 * @see net.jodah.expiringmap.ExpiringMap
 * ========================Cache the template enclosed========================
 * @see io.github.zpf9705.core.ExpireTemplate
 * @see io.github.zpf9705.core.StringExpiredTemplate
 * @see io.github.zpf9705.core.ExpireOperations
 * @see io.github.zpf9705.core.ValueOperations
 * @see io.github.zpf9705.core.DefaultValueOperations
 * @see io.github.zpf9705.core.AbstractExpireAccessor
 * ========================Overdue listener===================================
 * {@link io.github.zpf9705.core.ExpiringListener#expirationForBean()} In the name of the template bean listener
 * {@link io.github.zpf9705.core.ExpiringListeners#value()} Radio listening
 * =======================The persistent cache data to the hard disk============================
 * {@link io.github.zpf9705.core.ExpireMapCacheProperties}
 *  Persistence openPersistence means Whether to open the persistence
 *  Persistence persistencePath means Persistent path
 *  Persistence noPersistenceOfExpireTime means Persistence time
 *  Persistence noPersistenceOfExpireTimeUnit means Provisions of the unit
 * {@link io.github.zpf9705.core.AbstractGlobePersistenceIndicator} define persistent behavior
 * {@link io.github.zpf9705.core.ExpireGlobePersistence} the persistence method
 * {@link io.github.zpf9705.core.GlobePersistence}  define the persistence
 * {@link io.github.zpf9705.core.PersistenceExpiringCallback}  after listening to perform rear remove persistent file
 */
package io.github.zpf9705.core;

