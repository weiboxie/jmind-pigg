package jmind.pigg.mongodb.code;

/**
 * Created by weibo.xwb on 2017/12/19.
 */

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author neo
 */
public class DynamicInstanceBuilder<T> {
    private static final AtomicInteger INDEX = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(DynamicInstanceBuilder.class);
    private final CtClass classBuilder;
    private final ClassPool classPool;
    private Class[] constructorParamClasses;

    public DynamicInstanceBuilder(Class<?> interfaceClass, String className) {
        if (!interfaceClass.isInterface()) {
            throw new Error("interface class must be interface, interfaceClass=" + interfaceClass);
        }

        classPool = ClassPool.getDefault();
        classBuilder = classPool.makeClass(className + "$" + (INDEX.getAndIncrement()));

        try {
            classBuilder.addInterface(classPool.get(interfaceClass.getCanonicalName()));
            CtConstructor constructor = new CtConstructor(null, classBuilder);
            constructor.setBody(";");
            classBuilder.addConstructor(constructor);
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public DynamicInstanceBuilder<T> constructor(Class[] constructorParamClasses, String body) {
        if (this.constructorParamClasses != null) {
            throw new Error("dynamic class must have no more than one custom constructor");
        }
        try {
            this.constructorParamClasses = constructorParamClasses;
            CtClass[] params = new CtClass[constructorParamClasses.length];
            for (int i = 0; i < constructorParamClasses.length; i++) {
                Class<?> paramClass = constructorParamClasses[i];
                //  params[i] = classPool.getCtClass(paramClass.getCanonicalName());
            }
            CtConstructor constructor = new CtConstructor(params, classBuilder);
            constructor.setBody(body);
            classBuilder.addConstructor(constructor);
            return this;
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
return this;
    }



    public DynamicInstanceBuilder<T> addMethod(String method) {
        try {
            classBuilder.addMethod(CtMethod.make(method, classBuilder));
            return this;
        } catch (CannotCompileException e) {
            logger.error("method failed to compile:\n{}", method);
            e.printStackTrace();
        }
        return  this;
    }

    public DynamicInstanceBuilder<T> addField(String field) {
        try {
            classBuilder.addField(CtField.make(field, classBuilder));

        } catch (CannotCompileException e) {
            logger.error("field failed to compile:\n{}", field);
            e.printStackTrace();
        }
        return this;
    }

    public T build(Object... constructorParams)  {
        try {
            @SuppressWarnings("unchecked")
            Class<T> targetClass = classBuilder.toClass();
            classBuilder.detach();
            return targetClass.getDeclaredConstructor(constructorParamClasses).newInstance(constructorParams);
        } catch (CannotCompileException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
