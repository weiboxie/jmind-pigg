package jmind.pigg.mongodb;




import jmind.pigg.annotation.Id;
import jmind.pigg.mongodb.anno.Collection;
import jmind.pigg.mongodb.reflect.Types;
import jmind.pigg.mongodb.validate.TypeValidator;
import jmind.pigg.mongodb.validate.type.TypeVisitor;
import org.bson.types.ObjectId;



import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author neo
 */
public class MongoClassValidator implements TypeVisitor {
    private final TypeValidator validator;
    private final Map<String, Set<String>> fields = new HashMap<>();
    private boolean validateView;
    private Field id;

    public MongoClassValidator(Class<?> entityClass) {
        validator = new TypeValidator(entityClass);
        validator.allowedValueClass = this::allowedValueClass;
        validator.allowChildListAndMap = true;
        validator.allowChildObject = true;
        validator.visitor = this;
    }

    public void validateEntityClass() {
        validator.validate();

        if (id == null) {
            throw new Error("entity class must have @Id field, class={}"+ validator.type.getTypeName());
        }
    }

    public void validateViewClass() {
        validateView = true;
        validator.validate();
    }

    private boolean allowedValueClass(Class<?> valueClass) {
        return String.class.equals(valueClass)
                || ObjectId.class.equals(valueClass)
                || Integer.class.equals(valueClass)
                || Boolean.class.equals(valueClass)
                || Long.class.equals(valueClass)
                || Double.class.equals(valueClass)
                || LocalDateTime.class.equals(valueClass)
                || Enum.class.isAssignableFrom(valueClass);
    }

    @Override
    public void visitClass(Class<?> objectClass, String path) {
        if (!validateView && path == null && !objectClass.isAnnotationPresent(Collection.class))
            throw new Error("entity class must have @Collection, class={}"+ objectClass.getCanonicalName());
    }

    @Override
    public void visitField(Field field, String parentPath) {
        if (field.isAnnotationPresent(Id.class)) {
            validateId(field, parentPath == null);
        } else {
           jmind.pigg.mongodb.anno.Field mongoField = field.getDeclaredAnnotation(jmind.pigg.mongodb.anno.Field.class);
            if (mongoField == null) throw new Error("field must have @Field, field="+ field);
            String mongoFieldName = mongoField.name();

            Set<String> fields = this.fields.computeIfAbsent(parentPath, key -> new HashSet<>());
            if (fields.contains(mongoFieldName)) {
                throw new Error("field is duplicated, field={}, mongoField={}"+field+ mongoFieldName);
            }
            fields.add(mongoFieldName);
        }
    }

    private void validateId(Field field, boolean topLevel) {
        if (topLevel) {
            if (id != null)
                throw new Error("entity class must have only one @Id field, previous={}, current={}");
            Class<?> fieldClass = field.getType();
            if (!ObjectId.class.equals(fieldClass) && !String.class.equals(fieldClass)) {
                throw new Error("@Id field must be either ObjectId or String, field={}, class={}"+ Types.path(field)+fieldClass.getCanonicalName());
            }
            id = field;
        } else {
            throw new Error("child class must not have @Id field, field={}"+ field);
        }
    }
}

