package jmind.pigg.mongodb.validate;

import jmind.pigg.mongodb.anno.Min;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class MinValidator implements FieldValidator {
    private final String fieldPath;
    private final Min min;

    public MinValidator(String fieldPath, Min min) {
        this.fieldPath = fieldPath;
        this.min = min;
    }

    @Override
    public void validate(Object value, ValidationErrors errors, boolean partial) {
        if (value == null) return;

        double numberValue = ((Number) value).doubleValue();
        if (numberValue < min.value()) errors.add(fieldPath, min.message());
    }
}
