package jmind.pigg.mongodb.validate;

import jmind.pigg.exception.PiggException;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
public class Validator {
    final FieldValidator validator;

    Validator(FieldValidator validator) {
        this.validator = validator;
    }

    public void validate(Object instance) {
        validate(instance, false);
    }

    public void partialValidate(Object instance) {
        validate(instance, true);
    }

    private void validate(Object instance, boolean partial) {
        ValidationErrors errors = new ValidationErrors();
        validate(instance, errors, partial);
        if (errors.hasError())
            throw new PiggException(errors.errors.toString());
    }

    void validate(Object instance, ValidationErrors errors, boolean partial) {
        if (instance == null) {
            errors.add("instance", "instance must not be null");
        } else if (validator != null) { // validator can be null if no validation annotation presents
            validator.validate(instance, errors, partial);
        }
    }
}
