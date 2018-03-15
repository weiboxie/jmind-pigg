package jmind.pigg.mongodb.validate;

import java.util.List;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
public class MapValidator implements FieldValidator {
    private final List<FieldValidator> valueValidators;

    public MapValidator(List<FieldValidator> valueValidators) {
        this.valueValidators = valueValidators;
    }

    @Override
    public void validate(Object value, ValidationErrors errors, boolean partial) {
        if (value == null) return;
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) value;
        for (Map.Entry<String, ?> entry : ((Map<String, ?>) map).entrySet()) {
            for (FieldValidator valueValidator : valueValidators) {
                valueValidator.validate(entry.getValue(), errors, partial);
            }
        }
    }
}
