package jmind.pigg.annotation;

import jmind.pigg.datasource.AbstractDataSourceFactory;
import jmind.pigg.transaction.Isolation;

import java.lang.annotation.*;

/**
 * @Author: xieweibo
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
     */
    Class<? extends Throwable>[] rollbackFor() default {};
}
