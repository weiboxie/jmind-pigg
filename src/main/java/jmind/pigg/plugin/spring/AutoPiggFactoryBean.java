package jmind.pigg.plugin.spring;

import jmind.pigg.datasource.DataSourceFactory;
import jmind.pigg.datasource.SimpleDataSourceFactory;
import jmind.pigg.exception.InitializationException;
import jmind.pigg.interceptor.Interceptor;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.CacheHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/8/25.
 */
public class AutoPiggFactoryBean extends AbstractPiggFactoryBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Pigg createPigg() {
       // List<String> pkgs = AutoConfigurationPackages.get(applicationContext);
        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
        if(dataSourceMap.isEmpty()) {
            throw new InitializationException("must has dataSource");
        }
        List<DataSourceFactory> dsf=new ArrayList<>();
//        dataSourceMap.forEach((k,v)->{
//            dsf.add(new SimpleDataSourceFactory(k,v));
//        });
        for(Map.Entry<String,DataSource> entry:dataSourceMap.entrySet()){
            dsf.add(new SimpleDataSourceFactory(entry.getKey(),entry.getValue()));
        }
        Pigg mango = Pigg.newInstance(dsf);
        if(applicationContext.containsBean(CacheHandler.class.getName())){
            mango.setCacheHandler(applicationContext.getBean(CacheHandler.class));
        }

        // 添加拦截器
     Map<String, Interceptor> interceptorMap = applicationContext.getBeansOfType(Interceptor.class);
        if(!interceptorMap.isEmpty()){
            for(Map.Entry<String,Interceptor> entry:interceptorMap.entrySet()){
                mango.addInterceptor(entry.getValue());
            }
        }
        return  mango;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
