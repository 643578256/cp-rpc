package com.cp.rpc.client.net;

import com.cp.rpc.client.invoker.ClintInvoker;
import com.cp.rpc.client.invoker.Invoker;
import com.cp.rpc.client.net.netty.NettyClient;
import com.cp.rpc.common.center.zk.HostAndPort;
import com.cp.rpc.common.core.InvokerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnectManger {

    private static final Logger logger = LoggerFactory.getLogger(ClientConnectManger.class);

    public final static AtomicLong ID_GENER = new AtomicLong(0);

    private final static ConcurrentHashMap<String, CopyOnWriteArrayList<InvokerChannel>> connectionManager = new ConcurrentHashMap(32);

    private final static ConcurrentHashMap<Long, Invoker> PER_REQEUESTINFO = new ConcurrentHashMap(1024);

    public final static ConcurrentHashMap<HostAndPort, NettyClient> CLIENT_MANAGER = new ConcurrentHashMap(16);


    //connectionManager lock
    private final Lock lock = new ReentrantLock();

    private final static ClientConnectManger instance = new ClientConnectManger();

    public static ClientConnectManger getInstance() {
        return instance;
    }

    private ClientConnectManger() {
    }

    public List<InvokerChannel> getInvokerChannels(String interfaceName) {
        logger.info("===============getInvokerChannels================"+interfaceName);
        final CopyOnWriteArrayList<InvokerChannel> invokerChannels = connectionManager.get(interfaceName);
        if (invokerChannels == null) {
            final CopyOnWriteArrayList<InvokerChannel> newChannels = new CopyOnWriteArrayList();
            final CopyOnWriteArrayList<InvokerChannel> oldInvoker = connectionManager.putIfAbsent(interfaceName, newChannels);
            return oldInvoker != null ? oldInvoker : newChannels;
        }
        return invokerChannels;
    }

    public void putInvokerChannel(String interfaceName, InvokerChannel channelInfo) {
        try {
            lock.lock();
            logger.info("===============putInvokerChannel================"+interfaceName);
            CopyOnWriteArrayList<InvokerChannel> invokerChannels = new CopyOnWriteArrayList(connectionManager.get(interfaceName));
            if (invokerChannels.contains(channelInfo)) {
                invokerChannels.remove(channelInfo);
                channelInfo.getChannel().close();
            }
            invokerChannels.add(channelInfo);
            connectionManager.putIfAbsent(interfaceName, invokerChannels);
        } finally {
            lock.unlock();
        }
    }


    public void putRequestInfo(Long requestId, Invoker invoker) {
        PER_REQEUESTINFO.put(requestId, invoker);
    }

    public Invoker getReqeuestInfo(Long requestId) {
        Invoker rpcInvokerInfo = PER_REQEUESTINFO.get(requestId);
        PER_REQEUESTINFO.remove(requestId);
        return rpcInvokerInfo;
    }

    public ConcurrentHashMap<String, CopyOnWriteArrayList<InvokerChannel>> getConnectionManager() {
        return connectionManager;
    }
}
