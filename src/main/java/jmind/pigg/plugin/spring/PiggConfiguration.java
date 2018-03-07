package jmind.pigg.plugin.spring;

import jmind.base.util.reflect.ClassUtil;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.EnablePigg;
import jmind.pigg.exception.InitializationException;
import jmind.pigg.operator.cache.CacheHandler;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author weibo.xwb
 * @date 2017/8/25
 */
@Configuration
public class PiggConfiguration implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnablePigg.class.getName()));
        // 注册cache
        Class<?> cache = annoAttrs.getClass("cache");
        if(!cache.isInterface()){ // 不是接口就注册
            BeanDefinitionBuilder userBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cache);
             registry.registerBeanDefinition(CacheHandler.class.getName(),userBeanDefinitionBuilder.getRawBeanDefinition());
        }
        // 注册拦截器
        Class<?>[] interceptors = annoAttrs.getClassArray("interceptors");
        if(interceptors.length>0){ // 不会出现null
            for(Class clazz:interceptors){
                BeanDefinitionBuilder userBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                registry.registerBeanDefinition(clazz.getName(),userBeanDefinitionBuilder.getRawBeanDefinition());
            }
        }

        String[] basePackages=annoAttrs.getStringArray("basePackages");
        if(basePackages.length==0){ // 没有写明扫描包，默认加载main方法所在包。如果不是spring boot启动方式没有main方法抛异常

            Class mainClass= ClassUtil.deduceMainClass();
            if(mainClass==null){
                throw new InitializationException("没有扫描Dao需要加载");
            }
            basePackages=new String[]{mainClass.getPackage().getName()};
        }

        Class<?> factoryBean = annoAttrs.getClass("factoryBean");
        for (Class<?> daoClass : findDaoClasses(basePackages)) {
            GenericBeanDefinition bf = new GenericBeanDefinition();
            bf.setBeanClassName(daoClass.getName());
            MutablePropertyValues pvs = bf.getPropertyValues();
            pvs.addPropertyValue("daoClass", daoClass);
            bf.setBeanClass(factoryBean);
            bf.setPropertyValues(pvs);
            bf.setLazyInit(true);
            registry.registerBeanDefinition(daoClass.getName(), bf);
        }


    }



    private List<String> getPackages(String[] packages) {
        List<String> locationPatterns=new ArrayList<>();
        for (String p : packages) {
                String locationPattern = "classpath*:" + p.replaceAll("\\.", "/") + "/**/*" ;
                locationPatterns.add(locationPattern+   "Dao.class");
                locationPatterns.add(locationPattern+   "DAO.class");
        }
        return locationPatterns;
    }

    private List<Class<?>> findDaoClasses(String[] basePackages) {
        String dbAnnotationName=DB.class.getName();
        try {
            List<Class<?>> daos = new ArrayList<Class<?>>();
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (String locationPattern : getPackages(basePackages)) {
                Resource[] rs = resourcePatternResolver.getResources(locationPattern);
                for (Resource r : rs) {
                    MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
                    AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
                    if (annotationMD.hasAnnotation(dbAnnotationName)) {
                        ClassMetadata clazzMD = reader.getClassMetadata();
                        daos.add(Class.forName(clazzMD.getClassName()));
                    }
                }
            }
            return daos;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }





}
