package com.cp.rpc.client.net.netty.handler;

import cn.hutool.core.lang.Assert;
import com.cp.rpc.client.invoker.AbstarctInvker;
import com.cp.rpc.client.invoker.Invoker;
import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.common.code.RpcResponseMessageData;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CountDownLatch;


@ChannelHandler.Sharable
public class ClientChannelHandler extends SimpleChannelInboundHandler<RpcResponseMessageData> {


    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessageData msg) throws Exception {
        long responseId = msg.getResponseId();
        Invoker reqeuestInfo = ClientConnectManger.getInstance().getReqeuestInfo(responseId);

        Assert.notNull(reqeuestInfo);

        if(reqeuestInfo instanceof AbstarctInvker){
            AbstarctInvker info = (AbstarctInvker) reqeuestInfo;
            Object responseObj = msg.getResponseObj();
            info.setInvoker(responseObj);
            CountDownLatch countDownLatch = info.getCountDownLatch();
            countDownLatch.countDown();
        }
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }

    /**
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        //ConcurrentHashMap<String, CopyOnWriteArrayList<InvokerChannel>> connectionManager = ClientConnectManger.getConnectionManager();
        // 移除服务
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}
