package com.cp.rpc.serivce.net.handler;

import com.cp.rpc.common.code.RpcRequestMessageData;
import com.cp.rpc.common.handler.BizRequestReflectHandler;
import com.cp.rpc.common.handler.HandlerRequestMethod;
import com.cp.rpc.common.util.NamedThreadFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@ChannelHandler.Sharable
public class NettyServiceHanlder extends SimpleChannelInboundHandler<RpcRequestMessageData> {

    private Logger logger = LoggerFactory.getLogger(NettyServiceHanlder.class);

    //客户端 id.asLongText() , channel
    private static ConcurrentHashMap<String, Channel> connect_manager = new ConcurrentHashMap<String, Channel>(16);
    private static ConcurrentHashMap<Channel, AtomicInteger> connect_idl = new ConcurrentHashMap(16);

    private ThreadPoolExecutor threadPoolExecutor;

    protected Map<String, HandlerRequestMethod> serviceObject;

    public NettyServiceHanlder(Map<String, HandlerRequestMethod> serviceObject){
        int available = Runtime.getRuntime().availableProcessors();
        this.serviceObject = serviceObject;
        threadPoolExecutor = new ThreadPoolExecutor(available,available,60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("NettyServiceHanlder:"+hashCode()+":"));
    }

    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessageData msg) throws Exception {
        logger.info("channelRead0======={}",ctx.hashCode());
        if(msg.getRequestId()==0){
            return;
        }
        threadPoolExecutor.execute(new BizRequestReflectHandler(ctx,msg,serviceObject)); //可以做出可切换调用方式，反射或直接包装调用
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive======={}=====channel={}",ctx.hashCode(),ctx.channel());
        connect_manager.put(ctx.channel().id().asLongText(),ctx.channel());
        connect_idl.put(ctx.channel(),new AtomicInteger());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive======={}=====channel={}",ctx.hashCode(),ctx.channel());
        connect_manager.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event =  (IdleStateEvent)evt;
            logger.info("当前channel={}长时间没有心跳消息事件={}",ctx.channel().toString(),event);
            // 触发客户端读写心跳
            if(event.state() == IdleState.READER_IDLE){
                AtomicInteger times = connect_idl.get(ctx.channel());
                int i = times.incrementAndGet();
                if(i >= 3){
                    Channel channel = ctx.channel();
                    ChannelId id = channel.id();
                    String s = id.asLongText();
                    Channel mapChannel = connect_manager.get(s);
                    if(mapChannel == channel){
                        connect_manager.remove(s);
                    }
                    ctx.channel().close();

                }
            }
            if(event.state() == IdleState.ALL_IDLE){
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
