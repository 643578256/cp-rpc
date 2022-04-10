package com.cp.test.face.impl;

import com.cp.rpc.common.annotaion.RpcService;
import com.cp.test.face.TestAA;
import com.cp.test.face.TestDTO;
import org.springframework.stereotype.Component;

@Component
public class TestAAImpl implements TestAA {

    public void testA1(int a, TestDTO testDTO) {
        System.out.println(a+"----------------------"+testDTO);
    }

    public void testA2(String... ars) {
        System.out.println("-----------testA2-----------"+ars);
    }

    public TestDTO testA3(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println("---------testA3-------------"+a);
        }

        TestDTO testDTO =new TestDTO();
        testDTO.setAa("qerersdafasd");
        return testDTO;
    }
}
