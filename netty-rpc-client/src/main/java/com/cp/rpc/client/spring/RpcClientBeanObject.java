package com.cp.rpc.client.spring;

import com.cp.rpc.client.invoker.InterfaceInvocationHandler;
import com.cp.rpc.common.exten.Extentions;
import com.cp.rpc.common.proxy.IProxy;
import com.cp.rpc.common.proxy.jdk.ProxyForJDK;
import org.springframework.beans.factory.FactoryBean;

public class RpcClientBeanObject implements FactoryBean {
    public RpcClientBeanObject(){}
    public RpcClientBeanObject(Class<?> tClass) {
        this.tClass = tClass;
    }

    private Class<?> tClass;

    public Class<?> gettClass() {
        return tClass;
    }

    public void settClass(Class<?> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Object getObject() throws Exception {
        InterfaceInvocationHandler interfaceInvocationHandler = new InterfaceInvocationHandler(tClass.getName());
        return new ProxyForJDK(interfaceInvocationHandler).getProxy(tClass);
    }

    @Override
    public Class<?> getObjectType() {
        return tClass;
    }
}
