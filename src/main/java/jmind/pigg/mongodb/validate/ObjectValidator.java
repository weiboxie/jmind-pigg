package jmind.pigg.mongodb.validate;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class ObjectValidator implements FieldValidator {
    private final Map<Field, List<FieldValidator>> validators;

    public ObjectValidator(Map<Field, List<FieldValidator>> validators) {
        this.validators = validators;
    }

    @Override
    public void validate(Object instance, ValidationErrors errors, boolean partial) {
        if (instance != null) {
            validators.forEach((field, validators) -> {
                try {
                    Object fieldValue = field.get(instance);
                    validators.forEach(validator -> validator.validate(fieldValue, errors, partial));
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            });
        }
    }
}

