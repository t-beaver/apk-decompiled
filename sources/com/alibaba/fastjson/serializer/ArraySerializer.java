package com.alibaba.fastjson.serializer;

import com.taobao.weex.el.parse.Operators;
import java.io.IOException;
import java.lang.reflect.Type;

public class ArraySerializer implements ObjectSerializer {
    private final ObjectSerializer compObjectSerializer;
    private final Class<?> componentType;

    public ArraySerializer(Class<?> cls, ObjectSerializer objectSerializer) {
        this.componentType = cls;
        this.compObjectSerializer = objectSerializer;
    }

    public final void write(JSONSerializer jSONSerializer, Object obj, Object obj2, Type type, int i) throws IOException {
        SerializeWriter serializeWriter = jSONSerializer.out;
        if (obj == null) {
            serializeWriter.writeNull(SerializerFeature.WriteNullListAsEmpty);
            return;
        }
        Object[] objArr = (Object[]) obj;
        int length = objArr.length;
        SerialContext serialContext = jSONSerializer.context;
        jSONSerializer.setContext(serialContext, obj, obj2, 0);
        try {
            serializeWriter.append((char) Operators.ARRAY_START);
            for (int i2 = 0; i2 < length; i2++) {
                if (i2 != 0) {
                    serializeWriter.append((char) Operators.ARRAY_SEPRATOR);
                }
                Object obj3 = objArr[i2];
                if (obj3 == null) {
                    if (!serializeWriter.isEnabled(SerializerFeature.WriteNullStringAsEmpty) || !(obj instanceof String[])) {
                        serializeWriter.append((CharSequence) "null");
                    } else {
                        serializeWriter.writeString("");
                    }
                } else if (obj3.getClass() == this.componentType) {
                    this.compObjectSerializer.write(jSONSerializer, obj3, Integer.valueOf(i2), (Type) null, 0);
                } else {
                    jSONSerializer.getObjectWriter(obj3.getClass()).write(jSONSerializer, obj3, Integer.valueOf(i2), (Type) null, 0);
                }
            }
            serializeWriter.append((char) Operators.ARRAY_END);
        } finally {
            jSONSerializer.context = serialContext;
        }
    }
}
