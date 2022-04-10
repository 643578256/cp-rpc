package com.cp.rpc.common.handler;

import com.cp.rpc.common.Void;
import com.cp.rpc.common.code.*;
import com.cp.rpc.common.code.seri.HessianSerializer;
import com.cp.rpc.common.ex.RpcException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class BizRequestReflectHandler extends AbstractHandler implements RequestHandler{

    private  static Logger logger = LoggerFactory.getLogger(BizRequestReflectHandler.class);
    public BizRequestReflectHandler(ChannelHandlerContext ctx, RpcRequestMessageData messageData, Map<String, HandlerRequestMethod> serviceObject) {
        super(messageData, serviceObject);
        super.ctx = ctx;
    }

    @Override
    public void executeHandler() {
        beforeHandler();
        System.out.println("--------------------------");
        HandlerRequestMethod handlerMethods = serviceObject.get(buildeKey());
        Method method = handlerMethods.getMethod();
        RpcProtocolData protocalData = new RpcProtocolData();
        try{
            Class<?> returnType = method.getReturnType();
            Object invoke = method.invoke(handlerMethods.getRef(), messageData.getParameters());
            if("void".equals(returnType.getName())){
                invoke = new Void();
            }

            protocalData.setMagic(ProtocolConstant.magic);
            protocalData.setMessageType(MessageType.RESPONSE.getType());
            protocalData.setSessionId(messageData.getRequestId());
            protocalData.setVersion(Byte.valueOf(messageData.getVersion()+""));
            RpcResponseMessageData response = new RpcResponseMessageData();
            response.setResponseId(messageData.getRequestId());
            response.setResponseObj(invoke);
            byte[] bytes = HessianSerializer.SERIALIZER.enSerializer(response);
            protocalData.setContent(bytes);
            protocalData.setLength(bytes.length);
        }catch (Exception e){
            byte[] bytes = new byte[0];
            try {
                bytes = HessianSerializer.SERIALIZER.enSerializer(new RpcException(e.getMessage()));
            } catch (Exception ex) {
                logger.error("序列化异常",ex);
            }
            protocalData.setContent(bytes);
            protocalData.setLength(bytes.length);
        }
        ctx.writeAndFlush(protocalData).addListener(new GenericFutureListener(){
            @Override
            public void operationComplete(Future future) throws Exception {
                //可以做一些统计信息
                logger.info(future.get()+"消息回传到客户端====================");
            }
        });

    }

    @Override
    public void run() {
        executeHandler();
    }

}
