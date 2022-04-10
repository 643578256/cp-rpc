package com.cp.rpc.client.net;

import com.cp.rpc.common.util.NamedThreadFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract  class AbstractClient {

    protected String host;
    protected Integer port;
    protected AtomicBoolean isStrarted = new AtomicBoolean(false);

    private boolean epollEnabled = false;

    public AbstractClient(String host, Integer port, boolean epollEnabled) {
        this.host = host;
        this.port = port;
        this.epollEnabled = epollEnabled;
    }

    public String getClientInfo(){
        return host+":"+port;
    }

    protected EventLoopGroup createEventLoopGroup(int nThreads, final String threadNamePre){
        return epollEnabled ? new EpollEventLoopGroup(nThreads, new NamedThreadFactory(threadNamePre))
                : new NioEventLoopGroup(nThreads, new NamedThreadFactory(threadNamePre));
    }

    public Class<? extends SocketChannel> getEpollOrNio() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public boolean isEpollEnabled() {
        return epollEnabled;
    }

    public boolean getHasStart(){
        return isStrarted.get();
    }


}
