package com.cp.rpc.client.invoker;

import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.common.code.MessageType;
import com.cp.rpc.common.code.RpcProtocolData;
import com.cp.rpc.common.code.RpcRequestMessageData;
import com.cp.rpc.common.code.seri.HessianSerializer;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class ClintInvoker  extends AbstarctInvker {

    private static final Logger logger = LoggerFactory.getLogger(ClintInvoker.class);

    private Class zcalss;
    private Method method;
    private String interfaceName;
    private int version = 1;
    private Object[] args;

    public ClintInvoker(InvokerChannel invokerChannel,  Method method, String interfaceName, Object[] args) {
        super(invokerChannel,new CountDownLatch(1));
        this.method = method;
        this.interfaceName = interfaceName;
        this.args = args;
    }

    @Override
    protected Serializable buildRequestData() {
        RpcRequestMessageData mateData = new RpcRequestMessageData();
        mateData.setInterfaceName(interfaceName);
        mateData.setMethod(method.getName());
        mateData.setParameterTypes(method.getParameterTypes());
        mateData.setRequestId(ClientConnectManger.ID_GENER.incrementAndGet());
        mateData.setVersion(version);
        mateData.setParameters(args);


        RpcProtocolData protocolData = new RpcProtocolData();
        protocolData.setVersion((byte)mateData.getVersion().intValue());
        protocolData.setSessionId(mateData.getRequestId());
        protocolData.setMessageType(MessageType.REQUEST.getType());
        try {
            byte[] bytes = HessianSerializer.SERIALIZER.enSerializer(mateData);
            protocolData.setLength(bytes.length);
            protocolData.setContent(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcException("序列化异常");
        }
        return protocolData;
    }

    @Override
    protected void doAfterInvoker() {

    }

    @Override
    protected void sendedSoming() {
        logger.info("-----------------------invoker={}=消息发送完成--------------------",interfaceName+method.getName());
    }



}
