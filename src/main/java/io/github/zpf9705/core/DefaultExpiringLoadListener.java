package io.github.zpf9705.core;

/**
 * <p>
 *    default Expiring Load Listener of key{@link String} and value {@link String}
 * <p>
 *
 * @author zpf
 * @since 2.0.1
 */
public abstract class DefaultExpiringLoadListener implements ExpiringLoadListener<String,String>{

    @Override
    public abstract void load(String key, String value);
}
