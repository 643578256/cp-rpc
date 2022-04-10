package com.cp.rpc.serivce.core;

import cn.hutool.core.lang.Assert;
import com.cp.rpc.common.handler.HandlerRequestMethod;

import java.util.concurrent.ConcurrentHashMap;

public class RpcServerManager {

    /**
     * 调用服务 interfaceName
     */
    private final ConcurrentHashMap<String, HandlerRequestMethod> serviceObject = new ConcurrentHashMap(1024);


    public ConcurrentHashMap<String, HandlerRequestMethod> getServiceObject(){
        return serviceObject;
    }
    public void putService(HandlerRequestMethod requestMethod){
        Assert.notNull(requestMethod);
        serviceObject.putIfAbsent(buidMethodName(requestMethod),requestMethod);
    }
    private String buidMethodName(HandlerRequestMethod requestMethod){
        return requestMethod.getRealClass().getName()+"#"+requestMethod.getMethodName()+"#"+requestMethod.getVersion();
    }

}
