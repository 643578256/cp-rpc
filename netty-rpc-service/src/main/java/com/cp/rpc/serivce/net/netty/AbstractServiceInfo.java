package com.cp.rpc.serivce.net.netty;

import com.cp.rpc.common.util.NamedThreadFactory;
import com.cp.rpc.common.util.RpcConfig;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractServiceInfo {

    private static Map<String,Channel> ALL_ACTIVE = new ConcurrentHashMap<String, Channel>(16);

    protected volatile boolean isInlited = false;

    private String host;
    private boolean epollEnabled = false;

    public Channel getChannel(String hostAndPort){
        return ALL_ACTIVE.get(hostAndPort);
    }
    public boolean isActive(Channel channel){
        if(channel == null){
            return false;
        }
        return channel.isActive();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return RpcConfig.service_port_value;
    }


    public boolean isInlited() {
        return isInlited;
    }

    public synchronized void setInlited(boolean inlited) {
        isInlited = inlited;
    }

    protected EventLoopGroup createEventLoopGroup(int nThreads, final String threadNamePre){
        return epollEnabled ? new EpollEventLoopGroup(nThreads, new NamedThreadFactory(threadNamePre))
                : new NioEventLoopGroup(nThreads, new NamedThreadFactory(threadNamePre));
    }

    public Class<? extends ServerSocketChannel> getEpollOrNio() {
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public boolean isEpollEnabled() {
        return epollEnabled;
    }

    public Map<String,Channel> getChannels(){
        return ALL_ACTIVE;
    }
}
