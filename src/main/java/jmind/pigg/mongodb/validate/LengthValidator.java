package jmind.pigg.mongodb.validate;

import jmind.pigg.mongodb.anno.Length;

import java.util.List;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class LengthValidator implements FieldValidator {
    private final String fieldPath;
    private final Length length;

    public LengthValidator(String fieldPath, Length length) {
        this.fieldPath = fieldPath;
        this.length = length;
    }

    @Override
    public void validate(Object value, ValidationErrors errors, boolean partial) {
        if (value == null) return;

        int length;
        if (value instanceof String) {
            length = ((String) value).length();
        } else if (value instanceof List) {
            length = ((List) value).size();
        } else if (value instanceof Map) {
            length = ((Map) value).size();
        } else {
            throw new Error("unexpected value type, valueClass={}"+ value);
        }

        if (this.length.min() > -1 && length < this.length.min()
                || this.length.max() > -1 && length > this.length.max()) {
            errors.add(fieldPath, this.length.message());
        }
    }
}

