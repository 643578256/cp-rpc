package com.cp.rpc.client.spring;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcClientConfig {


    @Bean
    public RpcBuildOnInit rpcBuildOnInit(){
        return new RpcBuildOnInit();
    }

    @Bean
    public RpcInterfaceBeanDefinitionRegistry rpcInterfaceBeanDefinitionRegistry(){
        return new RpcInterfaceBeanDefinitionRegistry();
    }

    @Bean
    public IocRpcReferenceBean iocRpcReferenceBean(){
        return new IocRpcReferenceBean();
    }


    @Bean
    public ResourceApplicationContextInitializer resourceApplicationContextInitializer(){
        return new ResourceApplicationContextInitializer();
    }




}
