package com.cp.rpc.common.proxy.jdk;

import com.cp.rpc.common.proxy.IProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyForJDK implements IProxy {
    private InvocationHandler invocationHandler;
    public ProxyForJDK(InvocationHandler invocationHandler){
        this.invocationHandler = invocationHandler;
    }

    @Override
    public <T> T getProxy(Class zclass) {
        Object o = Proxy.newProxyInstance(IProxy.class.getClassLoader(), new Class[]{zclass}, invocationHandler);
        return (T)o;
    }

    @Override
    public <T> T getRef(Class zclass, Object proxy) {
        return null;
    }
}
