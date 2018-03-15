package jmind.pigg.mongodb.validate;

/**
 * Created by weibo.xwb on 2017/12/19.
 */



/**
 * @author neo
 */
interface FieldValidator {
    void validate(Object instance, ValidationErrors errors, boolean partial);
}