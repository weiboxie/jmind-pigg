package jmind.pigg.mongodb.anno;

/**
 * Created by weibo.xwb on 2017/12/20.
 */
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Length can be used on String, List and Map fields.
 *
 * @author neo
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Length {
    int min() default -1;

    int max() default -1;

    String message() default "the length of field must be within range";
}
