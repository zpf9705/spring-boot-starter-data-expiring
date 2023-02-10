package com.github.zpf.expiring.core;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

/**
 * <p>
 *     many types of key  and value serialization adapter
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 **/
public class ExpiringSerializerAdapter<T> implements ExpiringSerializer<T> {

    private final Class<T> type;

    public ExpiringSerializerAdapter(Class<T> type) {
        this.type = type;
    }

    @Override
    public byte[] serialize(T t) {
        byte[] serialize = SerializationUtils.serialize(t);
        Assert.notNull(serialize,
                "serialize failed ! t serialize is not null " + t);
        return serialize;
    }

    @Override
    public T deserialize(byte[] bytes) {
        T t = null;
        Object deserialize = SerializationUtils.deserialize(bytes);
        if (deserialize != null) {
            t = BeanUtil.copyProperties(deserialize, type);
        }
        return t;
    }

    @Override
    public Class<T> serializerType() {
        return type;
    }
}
