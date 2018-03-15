package jmind.pigg.mongodb.anno;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;



@Target(FIELD)
@Retention(RUNTIME)
public @interface Field {
    String name();
}