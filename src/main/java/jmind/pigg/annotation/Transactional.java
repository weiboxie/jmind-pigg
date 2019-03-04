package jmind.pigg.annotation;

import jmind.pigg.datasource.AbstractDataSourceFactory;
import jmind.pigg.transaction.Isolation;

import java.lang.annotation.*;

/**
 * @Author: xieweibo
 * https://blog.csdn.net/blueheart20/article/details/44654007
 * http://www.jfaster.org/transaction.html
 * @Date: 2018/4/17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

    // 对应的DataSource
    String value() default AbstractDataSourceFactory.DEFULT_NAME;

    // 事物隔离级别
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * 事务读写性
     */
    boolean readOnly() default false;

    /**
     * 一组异常类，遇到时回滚
     * https://blog.csdn.net/u012557814/article/details/50685374
     */
    Class<? extends Throwable>[] rollbackFor() default {RuntimeException.class,Error.class};
}
