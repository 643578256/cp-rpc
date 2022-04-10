package com.cp.rpc.client.net;

import com.cp.rpc.common.center.zk.HostAndPort;
import com.cp.rpc.common.core.InvokerChannel;
import io.netty.channel.Channel;

import java.util.List;

public interface IClient {

    void clientInit();

    void close(List<InvokerChannel> invokerChannels);

    Channel reConnection(InvokerChannel invokerChannel);


}
