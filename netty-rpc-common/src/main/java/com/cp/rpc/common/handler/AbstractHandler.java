package com.cp.rpc.common.handler;

import com.cp.rpc.common.code.RpcRequestMessageData;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //服务请求对象
    protected RpcRequestMessageData messageData;

    //服务对象，key为interface+method+version
    protected Map<String,HandlerRequestMethod> serviceObject ;

    protected ChannelHandlerContext ctx; //网络通道


    public AbstractHandler(RpcRequestMessageData messageData, Map<String,HandlerRequestMethod> serviceObject) {
        this.messageData = messageData;
        this.serviceObject = serviceObject;
    }

    public void beforeHandler(){
        logger.info("收到请求message={}",messageData);
        //数据校验
    }

    protected String buildeKey(){
        return messageData.getInterfaceName()+"#"+messageData.getMethod()+"#"+messageData.getVersion();
    }


}
