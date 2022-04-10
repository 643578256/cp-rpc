package com.cp.test.face;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TestConfigPropertes implements InitializingBean, EnvironmentAware {


    @Autowired
    private TestBB testBB;


    @Value("${rpc.cp.service.name}")
    private String aa;


    public void afterPropertiesSet() throws Exception {
        int a = 0;
    }

    public void setEnvironment(Environment environment) {

    }
}
