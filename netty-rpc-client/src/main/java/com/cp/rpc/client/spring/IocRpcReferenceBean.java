package com.cp.rpc.client.spring;

import com.cp.rpc.common.annotaion.RpcReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

/**
 *
 */
public class IocRpcReferenceBean extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware, MergedBeanDefinitionPostProcessor, PriorityOrdered {

    Logger logger = LoggerFactory.getLogger(IocRpcReferenceBean.class);

    private ApplicationContext applicationContext;

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        ReflectionUtils.doWithFields(aClass,(field) ->{
            if (Modifier.isStatic(field.getModifiers())) {
                if (logger.isInfoEnabled()) {
                    logger.info("RpcReference annotation is not supported on static fields: " + field);
                }
                return;
            }
            field.setAccessible(true);
            Object rObj = applicationContext.getBean(aClass);
            field.set(bean,rObj);
        },field -> {
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            return annotation != null;
        });
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
