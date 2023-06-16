package io.github.zpf9705.expiring.help;

/**
 * This interface is the core interface for implementing caching methods
 * and subsequent cache recovery and deletion of persistent files
 * Inherited the help center (i.e. cache operation entity) {@link HelpCenter}
 * and recovery center (i.e. implementation of subsequent cache recovery methods) {@link ReloadCenter}
 *
 * @author zpf
 * @since 3.0.5
 */
public interface Center<C, K, V> extends HelpCenter<C>, ReloadCenter<K, V> {
}
