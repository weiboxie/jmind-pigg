package jmind.pigg.mongodb.code;


import jmind.pigg.mongodb.reflect.GenericTypes;
import jmind.pigg.mongodb.reflect.Types;
import jmind.pigg.util.Strings;

import java.lang.reflect.Type;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public class CodeBuilder {
    public static String enumVariableLiteral(Enum value) {
        return value.getDeclaringClass().getCanonicalName() + "." + value.name();
    }

    public static String typeVariableLiteral(Type type) {
        if (GenericTypes.isList(type)) {
            return Types.class.getCanonicalName() + ".list(" + GenericTypes.listValueClass(type).getCanonicalName() + ".class)";
        } else {
            return GenericTypes.rawClass(type).getCanonicalName() + ".class";
        }
    }

    private final StringBuilder builder = new StringBuilder(256);

    public CodeBuilder append(String text) {
        builder.append(text);
        return this;
    }

    public CodeBuilder append(String pattern, Object... argument) {
        builder.append(Strings.format(pattern, argument));
        return this;
    }

    public CodeBuilder indent(int indent) {
        for (int i = 0; i < indent; i++)
            builder.append("    ");
        return this;
    }

    public String build() {
        return builder.toString();
    }
}
