package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;
import io.github.zpf9705.expiring.help.ExpireHelperFactory;

/**
 * Both key and value range operations are limited to {@link String} types.
 *
 * @author zpf
 * @since 1.1.0
 */
public class StringExpireTemplate extends ExpireTemplate<String, String> {

    private static final long serialVersionUID = 338557759999515451L;

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance.
     * and {@link #afterPropertiesSet()} still need to be called.
     */
    public StringExpireTemplate() {
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance ready to be used.
     *
     * @param helperFactory Helper factory for creating new connections
     */
    public StringExpireTemplate(ExpireHelperFactory helperFactory) {
        super();
        this.setHelperFactory(helperFactory);
        afterPropertiesSet();
    }
}
