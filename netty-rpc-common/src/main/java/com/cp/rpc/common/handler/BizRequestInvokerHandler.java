package com.cp.rpc.common.handler;

import com.cp.rpc.common.code.RpcRequestMessageData;

import java.util.Map;


/**
 * 通过javassits 直接调用
 */
public class BizRequestInvokerHandler extends AbstractHandler implements RequestHandler{

    public BizRequestInvokerHandler(RpcRequestMessageData messageData, Map<String, HandlerRequestMethod> serviceObject) {
        super(messageData, serviceObject);
    }

    @Override
    public void executeHandler() {

    }

    @Override
    public void run() {

    }
}
