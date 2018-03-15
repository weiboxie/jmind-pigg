package jmind.pigg.mongodb.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public final class Types {
    public static Type generic(Class<?> rawType, Type... arguments) {
        return new ParameterizedTypeImpl(rawType, arguments, null);
    }

    public static Type list(Type valueType) {
        return generic(List.class, valueType);
    }

    public static Type map(Type keyType, Type valueType) {
        return generic(Map.class, keyType, valueType);
    }

    public static Type supplier(Type valueType) {
        return generic(Supplier.class, valueType);
    }

    public static String path(Field field) {
        return field.getDeclaringClass().getTypeName() + "." + field.getName();
    }
}
