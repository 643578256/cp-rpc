package com.cp.rpc.common.core;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/***
 * 包装了，客户端请求调用的基本信息，共代理里使用
 * 且每个类对应一个网络链接
 */
public class InvokerChannel {
    private String host;
    private volatile int reconnectTime = 0;
    private volatile long clickTimes = 1;
    private volatile int weight = 1;
    private int port;
    //TODO   可以升级成链接池 提升性能
    private volatile Channel channel;

    public InvokerChannel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String hostAndPort(){
        return host+":"+port;
    }

    public boolean checkActive(){
        if (channel == null || !channel.isOpen() || !channel.isActive()){
            return false;
        }
        return channel.isOpen() && channel.isActive();
    }

    public long getClickTimes() {
        return clickTimes;
    }

    public void setClickTimes(long clickTimes) {
        this.clickTimes = clickTimes;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean close(){
        if(checkActive()){
            channel.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        InvokerChannel objInfo = (InvokerChannel) obj;
        return hostAndPort().equals(objInfo.hostAndPort());
    }
    public int addReconnecTime(){
        reconnectTime = reconnectTime++;
        return reconnectTime;
    }
}
