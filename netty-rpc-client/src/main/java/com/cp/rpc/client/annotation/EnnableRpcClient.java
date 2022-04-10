package com.cp.rpc.client.annotation;


import com.cp.rpc.client.spring.RpcClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(RpcClientConfig.class)
public @interface EnnableRpcClient {

}
