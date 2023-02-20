package io.github.zpf9705.core;

/**
 * <p>
 *     Provide fixed generic String String {@link ExpireTemplate}  is  here
 * </p>
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
     * @see ExpireTemplate#ExpireTemplate(String)
     * @param factoryBeanName ioc bean name
     */
    public StringExpiredTemplate(String factoryBeanName) {
        super(factoryBeanName);
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }
}
