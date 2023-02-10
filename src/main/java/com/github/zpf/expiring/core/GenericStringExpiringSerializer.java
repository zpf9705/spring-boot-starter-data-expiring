package com.github.zpf.expiring.core;

/**
 * <p>
 *     Type string serialization class {@link ExpiringSerializerAdapter}
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
public class GenericStringExpiringSerializer extends ExpiringSerializerAdapter<String> {

    public GenericStringExpiringSerializer() {
        super(String.class);
    }
}
