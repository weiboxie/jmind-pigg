package jmind.pigg.mongodb.validate.type;

import java.lang.reflect.Field;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public interface TypeVisitor {
    void visitClass(Class<?> objectClass, String path);

    void visitField(Field field, String parentPath);
}
