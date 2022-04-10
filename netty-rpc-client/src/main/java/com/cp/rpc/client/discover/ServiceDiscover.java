package com.cp.rpc.client.discover;

import com.cp.rpc.client.invoker.InterfaceInvocationHandler;
import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.client.net.netty.NettyClient;
import com.cp.rpc.common.center.InterfaceInfo;
import com.cp.rpc.common.center.zk.CuratorClient;
import com.cp.rpc.common.center.zk.HostAndPort;
import com.cp.rpc.common.core.InvokerChannel;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class ServiceDiscover extends AbstarctServerDiscovers {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscover.class);
    private CuratorClient curatorClient;
    private ClientConnectManger manger;
    private Executor executor = Executors.newFixedThreadPool(4);
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ServiceDiscover(CuratorClient client) {
        this.curatorClient = client;
        this.manger = ClientConnectManger.getInstance();
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public List<HostAndPort> findServer() throws Exception {
        try {
            Stat stat = curatorClient.ckeckNodeExist(CuratorClient.DEFAULT_NAME_SPACE_P);
            if (stat == null) {
                curatorClient.createRootService();
            }

            curatorClient.watchPathChildrenNode(CuratorClient.DEFAULT_NAME_SPACE_P, new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                    if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType())) {
                        //List<String> children = curatorClient.getChildren(CuratorClient.DEFAULT_NAME_SPACE_P);
                        buidServiceInterface(Arrays.asList(event.getData().getPath()), true, false);
                    } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())) {
                        buidServiceInterface(Arrays.asList(event.getData().getPath()), false, false);
                    }
                }
            });
            List<String> children = curatorClient.getChildren(CuratorClient.DEFAULT_NAME_SPACE_P);
            buidServiceInterface(children, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //key 为interface name ，value 为 服务列表
    private void buidServiceInterface(List<String> nodes, boolean isAdd, boolean initFindChildren) throws Exception {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        nodes.forEach(node -> {
            //executor.execute(new Runnable() {
               // @Override
             //   public void run() {
                    String[] serverNameAndHost = null;
                    if (!initFindChildren) {
                        String[] split = node.split("\\/");//node == /rpc-cp/providers/serviceName#ip:port
                        serverNameAndHost = split[2].split(CuratorClient.DEFAULT_SPLIT);// /serviceName#ip:port
                    } else {
                        serverNameAndHost = node.split(CuratorClient.DEFAULT_SPLIT);// /serviceName#ip:port
                    }
                    if (isAdd) {
                        try {
                            String serverNode = CuratorClient.DEFAULT_NAME_SPACE_S + "/" + serverNameAndHost[0]; // /service/xx服务名
                            Stat stat = curatorClient.ckeckNodeExist(serverNode);
                            if (stat == null) {
                                return;
                            }
                            byte[] data = curatorClient.getData(serverNode);
                            String interfaceStr = new String(data, "UTF-8");
                            ObjectMapper mapper = new ObjectMapper();
                            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, InterfaceInfo.class);
                            List<InterfaceInfo> list = mapper.readValue(interfaceStr, javaType);
                            String[] hostAndPort = serverNameAndHost[1].split("\\:");
                            //同一服务只需要一个长连接，后期可以改成链接池
                            InvokerChannel invokerChannel = new InvokerChannel(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
                            NettyClient nettyClient = new NettyClient(hostAndPort[0], Integer.parseInt(hostAndPort[1]), false);
                            nettyClient.clientInit();
                            nettyClient.reConnection(invokerChannel);

                            ClientConnectManger.CLIENT_MANAGER.putIfAbsent(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])), nettyClient);

                            for (InterfaceInfo ifi : list) {
                                List<InvokerChannel> hp = manger.getInvokerChannels(ifi.getInterfaceName());
                                hp.add(invokerChannel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Set<Map.Entry<String, CopyOnWriteArrayList<InvokerChannel>>> entries = ClientConnectManger.getInstance()
                                .getConnectionManager().entrySet();
                        Iterator<Map.Entry<String, CopyOnWriteArrayList<InvokerChannel>>> iterator = entries.iterator();
                        String[] hostAndPort = serverNameAndHost[1].split("\\:");
                        InvokerChannel invokerChannel = new InvokerChannel(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
                        while (iterator.hasNext()) {
                            Map.Entry<String, CopyOnWriteArrayList<InvokerChannel>> next = iterator.next();
                            CopyOnWriteArrayList<InvokerChannel> value = next.getValue();
                            for (InvokerChannel ic : value) {
                                if (ic.equals(invokerChannel)) {
                                    value.remove(ic);
                                }

                            }
                        }
                    }
                    int a =0;
               // }
            //});
        });
    }


}
