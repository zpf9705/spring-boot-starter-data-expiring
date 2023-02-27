package io.github.zpf9705.core;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.util.SerializationUtils;

/**
 * Key/value pairs serialized adapter , Direct implementation {@link ExpiringSerializer}
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
        AssertUtils.Operation.notNull(serialize,
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
