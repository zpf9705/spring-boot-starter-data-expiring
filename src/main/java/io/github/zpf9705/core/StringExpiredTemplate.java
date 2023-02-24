package io.github.zpf9705.core;

/**
 * Is to cache a default template mode key values for all of type String of derived classes
 *
 * @author zpf
 * @since 1.1.0
 */
public class StringExpiredTemplate extends ExpireTemplate<String, String> {

    private static final long serialVersionUID = 338557759999515451L;

    /**
     * @see ExpireTemplate#ExpireTemplate()
     */
    public StringExpiredTemplate() {
        super();
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }

    /**
     * @param factoryBeanName ioc bean name
     * @see ExpireTemplate#ExpireTemplate(String)
     */
    public StringExpiredTemplate(String factoryBeanName) {
        super(factoryBeanName);
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }
}
