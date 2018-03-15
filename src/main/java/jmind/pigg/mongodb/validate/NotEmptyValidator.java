package jmind.pigg.mongodb.validate;

import jmind.base.util.DataUtil;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class NotEmptyValidator implements FieldValidator {
    private final String fieldPath;
    private final String errorMessage;

    public NotEmptyValidator(String fieldPath, String errorMessage) {
        this.fieldPath = fieldPath;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value, ValidationErrors errors, boolean partial) {
        if (value == null) return;

        if (DataUtil.isEmpty((String) value)) errors.add(fieldPath, errorMessage);
    }
}
