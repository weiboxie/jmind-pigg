package jmind.pigg.mongodb.validate;

import jmind.pigg.mongodb.anno.Max;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class MaxValidator implements FieldValidator {
    private final String fieldPath;
    private final Max max;

    public MaxValidator(String fieldPath, Max max) {
        this.fieldPath = fieldPath;
        this.max = max;
    }

    @Override
    public void validate(Object value, ValidationErrors errors, boolean partial) {
        if (value == null) return;

        double numberValue = ((Number) value).doubleValue();
        if (numberValue > max.value()) errors.add(fieldPath, max.message());
    }
}
