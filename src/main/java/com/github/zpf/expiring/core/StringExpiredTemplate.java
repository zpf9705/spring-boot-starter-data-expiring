package com.github.zpf.expiring.core;

import java.util.Map;

/**
 * {@link ExpireTemplate} <string,string>
 * provider {@link ValueOperations}  <string,string>
 * @rule key == {@link String}
 * @rule value == {@link String}
 * and you can {@link Clearable} to {@link Map#clear()}
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
     */
    public StringExpiredTemplate(String factoryBeanName) {
        super(factoryBeanName);
        this.setKeySerializer(new GenericStringExpiringSerializer());
        this.setValueSerializer(new GenericStringExpiringSerializer());
    }
}
