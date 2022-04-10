package com.cp.rpc.serivce.spring;

import cn.hutool.core.util.ArrayUtil;
import com.cp.rpc.common.annotaion.RpcService;
import com.cp.rpc.common.ex.RpcException;
import com.cp.rpc.common.handler.HandlerRequestMethod;
import com.cp.rpc.common.util.RpcConfig;
import com.cp.rpc.serivce.core.RpcServerManager;
import com.cp.rpc.serivce.net.INetService;
import com.cp.rpc.serivce.net.netty.NettyService;
import com.cp.rpc.serivce.register.zk.ZkRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class RpcServerStart implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware, EnvironmentAware, DisposableBean {

    Logger logger = LoggerFactory.getLogger(RpcServerStart.class);

    private final RpcServerManager rpcServerManager = new RpcServerManager();

    private ApplicationContext applicationContext;

    private Environment environment;

    private INetService netService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //1.构建导出服务
        logger.info("===================开始导入服务接口===================");
        RpcInterfaceScanner s = new RpcInterfaceScanner();
        s.resetFilters(false);
        s.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
        String rpc_backgrad = environment.getProperty(RpcConfig.rpc_backgrad);
        List<Class> classes = s.doScan(rpc_backgrad);
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class next = iterator.next();
            Method[] declaredMethods = next.getDeclaredMethods();
            if (ArrayUtil.isNotEmpty(declaredMethods)) {
                Object bean = applicationContext.getBean(next);

                Assert.notNull(bean, "没有获取到对应类型的在SPRING中的对象class=" + next.getName());
                for (int i = 0; i < declaredMethods.length; i++) {
                    HandlerRequestMethod requestMethod = new HandlerRequestMethod(bean, next, declaredMethods[i]);
                    rpcServerManager.putService(requestMethod);
                }
            }
        }
        logger.info("===================结束导入服务接口===================");
        //2.注册服务
        logger.info("===================开始注册服务接口===================");
        ZkRegisterService zkRegister = new ZkRegisterService(classes,environment);
        try {
            zkRegister.registerService();

            //3.启动rpc NIO服务
            netService = new NettyService();
            netService.putServiceObject(rpcServerManager.getServiceObject());
            netService.createService();
        } catch (Exception e) {
            logger.error("注册服务到zk异常", e);
            throw new RpcException("注册服务到zk异常", e);
        }
        logger.info("===================RPC服务启动完成===================");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        if(environment instanceof StandardEnvironment){
            StandardEnvironment se = (StandardEnvironment) environment;
            MutablePropertySources propertySources = se.getPropertySources();
            try {
                //File file = ResourceUtils.getFile("classpath:cp-rpc.properties");
                Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("cp-rpc.properties"));
                propertySources.addLast(
                        new SystemEnvironmentPropertySource("cpRpcEnvironment", new HashMap(properties)));
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void destroy() throws Exception {
        netService.closeService();
    }


    static class RpcInterfaceScanner extends ClassPathScanningCandidateComponentProvider {
        private BeanDefinitionRegistry registry;

        private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

        private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;


        protected List<Class> doScan(String... basePackages) {
            Assert.notEmpty(basePackages, "At least one base package must be specified");
            List<Class> beanDefinitions = new ArrayList<>();
            for (String basePackage : basePackages) {
                List<Class> candidates = scanCandidateComponents(basePackage);
                beanDefinitions.addAll(candidates);
            }
            return beanDefinitions;
        }


        private List<Class> scanCandidateComponents(String basePackage) {
            List<Class> candidates = new ArrayList<>();
            try {
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        resolveBasePackage(basePackage) + '/' + "**/*.class";
                Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
                for (Resource resource : resources) {
                    logger.trace("Scanning " + resource);
                    if (resource.isReadable()) {
                        try {
                            MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                            if (isCandidateComponent(metadataReader)) {
                                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                                Class<?> aClass = Class.forName(annotationMetadata.getClassName());
                                candidates.add(aClass);
                            }
                        } catch (Throwable ex) {
                            throw new BeanDefinitionStoreException(
                                    "Failed to read candidate component class: " + resource, ex);
                        }
                    }
                }
                return candidates;
            } catch (IOException ex) {
                throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
            }
        }

        private ResourcePatternResolver getResourcePatternResolver() {
            return new PathMatchingResourcePatternResolver();
        }


    }
}
