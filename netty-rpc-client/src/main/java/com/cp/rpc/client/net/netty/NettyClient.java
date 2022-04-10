package com.cp.rpc.client.net.netty;

import com.cp.rpc.client.net.AbstractClient;
import com.cp.rpc.client.net.IClient;
import com.cp.rpc.client.net.netty.handler.ClientChannelHandler;
import com.cp.rpc.client.net.netty.handler.ClientHeartbeatHandler;
import com.cp.rpc.common.center.zk.HostAndPort;
import com.cp.rpc.common.code.ByteToMessageDecoderHandler;
import com.cp.rpc.common.code.MessageToByteEncodingHandler;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.util.NamedThreadFactory;
import com.cp.rpc.common.util.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyClient extends AbstractClient implements IClient {

    Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap client;

    private EventLoopGroup eventLoopGroup;


    private final Lock lock = new ReentrantLock();


    public NettyClient(String host, Integer port, boolean epollEnabled) {
        super(host, port, epollEnabled);
    }


    public void clientInit() {
        if(getHasStart()){
            return;
        }
        isStrarted.compareAndSet(false,true);
        client =  new Bootstrap();
        eventLoopGroup  = createEventLoopGroup(4,"client-"+getClientInfo());
        client.group(eventLoopGroup).channel(getEpollOrNio())
                .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(NettyConfig.TCP_NODELAY_DEFAULT))
                .option(ChannelOption.SO_REUSEADDR, Boolean.parseBoolean(NettyConfig.TCP_SO_REUSEADDR_DEFAULT))
                .option(ChannelOption.SO_KEEPALIVE, Boolean.parseBoolean(NettyConfig.TCP_SO_KEEPALIVE_DEFAULT));
        int lowWaterMark = NettyConfig.NETTY_BUFFER_LOW_WATERMARK_DEFAULT;
        int highWaterMark = NettyConfig.NETTY_BUFFER_HIGH_WATERMARK_DEFAULT;
        // init netty write buffer water mark
        /*client.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                lowWaterMark, highWaterMark));*/
        // init byte buf allocator
        if (/*NettyConfig.getValue(null,NettyConfig.)*/true) {
            this.client.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.client.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }
        final ClientHeartbeatHandler heartbeatHandler = new ClientHeartbeatHandler();
        final ClientChannelHandler clientChannelHandler = new ClientChannelHandler();
        client.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", new ByteToMessageDecoderHandler());
                pipeline.addLast("encoder", new MessageToByteEncodingHandler());

                if (true) {
                    pipeline.addLast("idleStateHandler",
                            new IdleStateHandler(0, 0, 20,
                                    TimeUnit.SECONDS));
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                }

                //pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler",clientChannelHandler);
            }
        });
    }

    public void close(List<InvokerChannel> invokerChannels) {
        for (InvokerChannel ic: invokerChannels) {
            ic.close();
        }
    }

    public Channel reConnection(InvokerChannel invokerChannel) {
        Channel channel = null;
        try{
            lock.lock();
            if (!invokerChannel.checkActive()){
                ChannelFuture sync = null;
                try {
                    sync = client.connect(new InetSocketAddress(host, port)).sync();
                } catch (Exception e) {
                    logger.error("重链接异常="+host+":"+port,e);
                }
                channel = sync.channel();
                invokerChannel.setChannel(channel);
            }

        }finally {
            lock.unlock();
        }
        return channel;
    }

}
