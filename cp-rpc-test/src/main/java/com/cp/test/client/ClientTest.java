package com.cp.test.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.cp.rpc.client.annotation.EnnableRpcClient;
import com.cp.rpc.common.router.RandomRouter;
import com.cp.test.face.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Configuration
@EnnableRpcClient
//@ImportResource(locations = "classpath:application-rpc.xml")
//@ComponentScan("com.cp.test.face")
public class ClientTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientTest.class);
        try {
            final TestAA bean = context.getBean(TestAA.class);
            final TestDTO dto = new TestDTO();
            dto.setAa("12111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
            dto.setAabb(233);
            System.out.println("=============AnnotationConfigApplicationContext================");
            for (int j = 0; j < 1000; j++) {
                ThreadUtil.execAsync(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10000; i++) {

                            System.out.println("=============Anno===========================" + i);
                            bean.testA1(99, dto);

                            bean.testA3(new int[]{1, 2});
                            bean.testA2("我", "的", "哦");
                            try {
                                Thread.sleep(RandomUtil.randomInt(100));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* TestAA bean = context.getBean(TestAA.class);
        TestBB beanb = context.getBean(TestBB.class);*/
    }
}
