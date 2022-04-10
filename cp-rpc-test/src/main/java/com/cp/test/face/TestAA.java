package com.cp.test.face;

import com.cp.rpc.common.annotaion.RpcService;

@RpcService
public interface TestAA {
    void testA1(int a,TestDTO testDTO);

    void testA2(String ... ars);

    TestDTO testA3(int[] a);
}
