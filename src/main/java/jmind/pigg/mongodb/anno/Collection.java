package jmind.pigg.mongodb.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by weibo.xwb on 2017/12/19.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Collection {
    String name();
}
