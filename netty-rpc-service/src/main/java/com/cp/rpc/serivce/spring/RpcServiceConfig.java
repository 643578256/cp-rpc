package com.cp.rpc.serivce.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServiceConfig {


    @Bean
    public RpcServerStart rpcServerStart() {
        return new RpcServerStart();
    }

}
