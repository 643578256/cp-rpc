package com.cp.rpc.common.center;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;


/**
 * 配置，可以通过spring boot 自动装配
 */
@Configuration
public class CenterConfigProperties {
    private final static String zkConnectionStr = "cp.rpc.zk.connection.str";

    @Resource
    private Environment environment;

    public String getZkConnectionStr(){
        String property = null;
        if(environment != null){
            property = environment.getProperty(zkConnectionStr);
        }
        if(property == null){
            property = System.getProperty(zkConnectionStr);
        }

        return property;
    }

}
