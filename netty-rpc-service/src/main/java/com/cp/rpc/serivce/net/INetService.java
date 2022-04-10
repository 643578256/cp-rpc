package com.cp.rpc.serivce.net;

import com.cp.rpc.common.handler.HandlerRequestMethod;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

public interface INetService{

    void initNetConnect();

    public void putServiceObject(Map<String, HandlerRequestMethod> serviceObject) ;

    void createService() throws InterruptedException;

    void closeService();


}
