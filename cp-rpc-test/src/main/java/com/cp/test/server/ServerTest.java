package com.cp.test.server;

import com.cp.rpc.client.annotation.EnnableRpcClient;
import com.cp.rpc.serivce.annotation.EnnableRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
@EnnableRpcServer
//@ImportResource(locations = "classpath:application-rpc.xml")
@ComponentScan("com.cp.test.face")
public class ServerTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerTest.class);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* TestAA bean = context.getBean(TestAA.class);
        TestBB beanb = context.getBean(TestBB.class);*/
    }
}