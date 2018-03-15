package jmind.pigg.mongodb.anno;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ValueNotNull {
    String message() default "value must not be null";
}