package jmind.pigg.annotation;


import jmind.pigg.interceptor.Interceptor;
import jmind.pigg.operator.cache.CacheHandler;
import jmind.pigg.plugin.page.MySQLPageInterceptor;
import jmind.pigg.plugin.spring.AbstractPiggFactoryBean;
import jmind.pigg.plugin.spring.AutoPiggFactoryBean;
import jmind.pigg.plugin.spring.PiggConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by weibo.xwb on 2017/8/25.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PiggConfiguration.class)
public @interface EnablePigg {

     // 扫描包，默认main方法的所在包
    String[] basePackages() default {};

    //创建pigg的factoryBean
    Class<? extends AbstractPiggFactoryBean> factoryBean() default AutoPiggFactoryBean.class;
    // 拦截器,默认添加mysql分页
    Class<? extends Interceptor>[]   interceptors() default MySQLPageInterceptor.class;
    // 缓存
    Class<? extends CacheHandler>  cache() default CacheHandler.class;
}
