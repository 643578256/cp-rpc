package com.cp.test.face;

import com.cp.rpc.common.annotaion.RpcService;

@RpcService
public interface TestBB {
    void testB1(int a, TestDTO testDTO);

    void testB2();

    TestDTO testB3();
}
