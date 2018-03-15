package jmind.pigg.mongodb;

import jmind.pigg.annotation.Id;
import jmind.pigg.mongodb.validate.Validator;
import jmind.pigg.mongodb.validate.ValidatorBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by weibo.xwb on 2017/12/19.
 */
class MongoEntityValidator {
    private final Map<Class<?>, Validator> validators = new HashMap();

    public void register(Class<?> entityClass) {
        new MongoClassValidator(entityClass).validateEntityClass();

        validators.computeIfAbsent(entityClass,
                key -> new ValidatorBuilder(key, field -> {
                    if (field.isAnnotationPresent(Id.class)) return "_id";
                    return field.getDeclaredAnnotation(jmind.pigg.mongodb.anno.Field.class).name();
                }).build());
    }

    public <T> void validate(T entity) {
        if (entity == null) throw new Error("entity must not be null");

        Validator validator = validators.get(entity.getClass());
        if (validator == null)
            throw new Error("entity class is not registered, entityClass={}"+entity.getClass().getCanonicalName());

        validator.validate(entity);
    }
}

