package io.github.zpf9705.core;

/**
 * Type string serialization class implementation of {@link ExpiringSerializerAdapter}
 *
 * @author zpf
 * @since 1.1.0
 **/
public class GenericStringExpiringSerializer extends ExpiringSerializerAdapter<String> {

    public GenericStringExpiringSerializer() {
        super(String.class);
    }
}
