package jmind.pigg.mongodb.validate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
class ValidationErrors {
    public Map<String, String> errors;

    public void add(String field, String error) {
        if (errors == null) errors = new LinkedHashMap<>();
        errors.put(field, error);
    }

    public boolean hasError() {
        return errors != null;
    }
}
