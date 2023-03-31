package io.github.zpf9705.expiring.core;

import io.github.zpf9705.expiring.connection.ExpireConnectionFactory;
import io.github.zpf9705.expiring.core.serializer.GenericStringExpiringSerializer;

/**
 * String-focused extension of Expire Template
 *
 * @author zpf
 * @since 1.1.0
 */
public class StringExpireTemplate extends ExpireTemplate<String, String> {

    private static final long serialVersionUID = 338557759999515451L;

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance. {@link #setConnectionFactory(ExpireConnectionFactory)}}
     * and {@link #afterPropertiesSet()} still need to be called.
     */
    public StringExpireTemplate() {
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }

    /**
     * Constructs a new <code>StringExpireTemplate</code> instance ready to be used.
     *
     * @param connectionFactory connection factory for creating new connections
     */
    public StringExpireTemplate(ExpireConnectionFactory connectionFactory) {
        super();
        this.setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }
}
