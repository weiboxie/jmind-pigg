package jmind.pigg.mongodb.validate;

import java.util.List;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class ListValidator implements FieldValidator {
    private final List<FieldValidator> valueValidators;

    public ListValidator(List<FieldValidator> valueValidators) {
        this.valueValidators = valueValidators;
    }

    @Override
    public void validate(Object list, ValidationErrors errors, boolean partial) {
        if (list == null) return;

        for (Object value : (List<?>) list) {
            for (FieldValidator valueValidator : valueValidators) {
                valueValidator.validate(value, errors, partial);
            }
        }
    }
}
