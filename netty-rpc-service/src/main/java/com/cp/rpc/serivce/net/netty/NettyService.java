package com.cp.rpc.serivce.net.netty;

import com.cp.rpc.common.code.ByteToMessageDecoderHandler;
import com.cp.rpc.common.code.MessageToByteEncodingHandler;
import com.cp.rpc.common.handler.HandlerRequestMethod;
import com.cp.rpc.common.util.NettyConfig;
import com.cp.rpc.serivce.net.handler.NettyServiceHanlder;
import com.cp.rpc.serivce.net.INetService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NettyService extends AbstractServiceInfo implements INetService {

    private static Logger logger = LoggerFactory.getLogger(NettyService.class);

    private static int idleTime = 3;

    private ServerBootstrap bootstrap = null;

    private Map<String, HandlerRequestMethod> serviceObject;



    public void initNetConnect() {

    }

    @Override
    public void putServiceObject(Map<String, HandlerRequestMethod> serviceObject) {
        this.serviceObject = serviceObject;
    }

    public void createService() throws InterruptedException {
        bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = createEventLoopGroup(1, "netty-boss"); //TODO

        EventLoopGroup workerGroup = createEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, "netty-work"); //TODO
        try{


        this.bootstrap.group(bossGroup, workerGroup)
                .channel(getEpollOrNio())
                .option(ChannelOption.SO_BACKLOG, Integer.parseInt(NettyConfig.TCP_SO_BACKLOG_DEFAULT))
                .option(ChannelOption.SO_REUSEADDR, Boolean.parseBoolean(NettyConfig.TCP_SO_REUSEADDR_DEFAULT));
        /*if (Boolean.parseBoolean(NettyConfig.NETTY_BUFFER_POOLED_DEFAULT)) {
            this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }*/

        this.bootstrap.childOption(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(NettyConfig.TCP_NODELAY_DEFAULT));
        this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.parseBoolean(NettyConfig.TCP_SO_KEEPALIVE_DEFAULT));
        int lowWaterMark = NettyConfig.NETTY_BUFFER_LOW_WATERMARK_DEFAULT;
        int highWaterMark = NettyConfig.NETTY_BUFFER_HIGH_WATERMARK_DEFAULT;
        //借鉴 成熟rpc的channel高低水位配置
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                lowWaterMark, highWaterMark));

        //TODO  后续补齐
        NettyServiceHanlder rpcHandler = new NettyServiceHanlder(serviceObject);
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){

            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //pipeline 上的handler 是从头开始，出是从尾部开始
                // 图例---> in为进才会进入的handler  out 为出  dup-为进出
                //进来最新做的解码，所以第一个是 解码
                //第二个就是出的 tail 节点的最后一个。是编码。
                //pipeline 理解图比如 加第一个 addLast 图为head-> decoding_in ->tail
                //加第二个 addLast 图为head -> in-> encoding_out ->tail
                //加第三个 就可以根据handler出来的情况比如这里是个
                // 空闲检查 addLast 图为head -> decoding_in-> encoding_out-> -> idle_dup ->tail

                pipeline.addLast("decoder",new ByteToMessageDecoderHandler());
                pipeline.addLast("encoder", new MessageToByteEncodingHandler());
                pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, 20 * idleTime,
                            TimeUnit.SECONDS));
                pipeline.addLast("handler", rpcHandler);
            }
        });
            ChannelFuture bind = bootstrap.bind(getLocalIp(),getPort()).sync();
            bind.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void closeService() {
        Collection<Channel> values = getChannels().values();
        for (Channel channel: values) {
            channel.close();
        }
    }


    public void enableTriggeredMode(ServerBootstrap serverBootstrap) {
        if (super.isEpollEnabled()) {
            serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE,
                    EpollMode.LEVEL_TRIGGERED);

        } else {
            serverBootstrap
                    .childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }
    }

    private String getLocalIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }
}
