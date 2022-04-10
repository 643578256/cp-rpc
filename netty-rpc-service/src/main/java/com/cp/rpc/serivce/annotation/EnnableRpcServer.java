package com.cp.rpc.serivce.annotation;


import com.cp.rpc.serivce.spring.RpcServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(RpcServiceConfig.class)
public @interface EnnableRpcServer {
}
