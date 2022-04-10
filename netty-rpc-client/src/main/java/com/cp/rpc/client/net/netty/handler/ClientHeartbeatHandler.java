package com.cp.rpc.client.net.netty.handler;

import com.cp.rpc.common.code.MessageType;
import com.cp.rpc.common.code.RpcProtocolData;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ClientHeartbeatHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeatHandler.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.info("触发心跳============={}",((IdleStateEvent) evt).state());
           // 触发客户端读写心跳
            RpcProtocolData protocolData = new RpcProtocolData();
            protocolData.setSessionId(0);
            protocolData.setMessageType(MessageType.PING.getType());
            protocolData.setLength(0);
            ctx.writeAndFlush(protocolData);
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
