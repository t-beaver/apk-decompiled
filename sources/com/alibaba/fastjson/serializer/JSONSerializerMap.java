package com.alibaba.fastjson.serializer;

import java.lang.reflect.Type;

@Deprecated
public class JSONSerializerMap extends SerializeConfig {
    public final boolean put(Class<?> cls, ObjectSerializer objectSerializer) {
        return super.put((Type) cls, objectSerializer);
    }
}
