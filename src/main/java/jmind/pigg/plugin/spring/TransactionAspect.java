package jmind.pigg.plugin.spring;

import jmind.base.annotation.CacheMonitor;
import jmind.pigg.annotation.Transactional;
import jmind.pigg.transaction.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * @Author: xieweibo
 * @Date: 2018/4/17
 */
@Aspect
public class TransactionAspect {

    @Pointcut("@annotation(jmind.pigg.annotation.Transactional)")
    public void limit() {
    }

    @Around("limit()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        final Transactional m = getAnnotation(pjp, Transactional.class);
        Transaction tx = TransactionFactory.newTransaction(m.value());
        try {
            Object r = pjp.proceed();
            tx.commit();
            return r;
        } catch (Throwable e) {
            // 只有是指定的异常才回滚，默认是 RuntimeException 和ERROR
            for(Class<? extends Throwable> exs:m.rollbackFor()){
                //if(exs.isAssignableFrom(e.getClass())){
                if(exs.isInstance(e)){
                    tx.rollback();
                    throw e;
                }
            }

            tx.commit();
            throw e;

        }




    }


    protected void execute(ProceedingJoinPoint pjp) {
        final Transactional m = getAnnotation(pjp, Transactional.class);
        TransactionTemplate.execute(m.value(),new TransactionAction() {

            @Override
            public void doInTransaction(TransactionStatus status) {

            }
        });
    }


    private final static <T extends Annotation> T getAnnotation(final ProceedingJoinPoint pjp, Class<T> cla) {
        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        final Method targetMethod = methodSignature.getMethod();
        return targetMethod.getAnnotation(cla);
    }



}
