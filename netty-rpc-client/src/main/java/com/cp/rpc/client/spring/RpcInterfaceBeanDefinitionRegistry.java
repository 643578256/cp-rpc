package com.cp.rpc.client.spring;

import com.cp.rpc.common.annotaion.RpcService;
import com.cp.rpc.common.util.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class RpcInterfaceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(RpcInterfaceBeanDefinitionRegistry.class);

    private Environment environment;

    /**
     * 注入bean的两种方式
     * 1：实现 BeanDefinitionRegistryPostProcessor
     * 一般用来把具有需要手动注册到spring的 Bean注册 到spring
     * 一般有特殊功能，比 BeanFactoryPostProcessor#postProcessBeanFactory的这方法更早调用
     * 所以：
     * postProcessBeanDefinitionRegistry 用来手动注册  BeanDefinition
     * postProcessBeanFactory 这个方法一般用来获取BeanDefinition，做操作
     * 2：使用自定义注解，再在自定义注解上加上 @Import 导入 ImportBeanDefinitionRegistrar的实现类
     *
     * @param registry
     * @throws BeansException
     * @see Import
     * @see ImportBeanDefinitionRegistrar
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("------------postProcessBeanDefinitionRegistry----------------");

        String basePackages = environment.getProperty(RpcConfig.rpc_backgrad);
        RpcInterfaceScanner rpcInterfaceScanner = new RpcInterfaceScanner(registry);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RpcService.class);
        rpcInterfaceScanner.addIncludeFilter(annotationTypeFilter);
        rpcInterfaceScanner.setEnvironment(environment);
        rpcInterfaceScanner.doScan(basePackages);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("------------postProcessBeanFactory----------------");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        //解决 spring自带的读取属性文件在上面方面之后的问题
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

    class RpcInterfaceScanner extends ClassPathScanningCandidateComponentProvider {
        private BeanDefinitionRegistry registry;

        private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

        private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

        public RpcInterfaceScanner(BeanDefinitionRegistry registry) {
            super(false);
            this.registry = registry;
        }

        public void scan(String... basePackages) {

            doScan(basePackages);
        }

        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Assert.notEmpty(basePackages, "At least one base package must be specified");
            Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
            for (String basePackage : basePackages) {
                Set<BeanDefinition> candidates = scanCandidateComponents(basePackage);
                for (BeanDefinition candidate : candidates) {
                    if (candidate instanceof AbstractBeanDefinition) {
                        AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) candidate;
                        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(beanDefinition);
                        beanDefinition.setScope(scopeMetadata.getScopeName());
                        String beanName = this.beanNameGenerator.generateBeanName(beanDefinition, this.registry);
                        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanDefinition.getBeanClassName(),"java.lang.Class");
                        beanDefinition.setBeanClassName(RpcClientBeanObject.class.getName());
                        //beanDefinition.setBeanClass();
                        beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
                        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                        beanDefinitions.add(definitionHolder);
                        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
                    }
                }
            }
            return beanDefinitions;
        }


        private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
            Set<BeanDefinition> candidates = new LinkedHashSet<>();
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
                                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                                sbd.setSource(resource);
                                //if (isCandidateComponent(sbd)) {
                                    candidates.add(sbd);
                                //}
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
