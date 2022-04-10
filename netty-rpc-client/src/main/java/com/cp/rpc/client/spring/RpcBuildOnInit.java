package com.cp.rpc.client.spring;

import com.cp.rpc.client.discover.ServiceDiscover;
import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.common.center.zk.CuratorClient;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import com.cp.rpc.common.util.RpcConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 在sprig 初始化完成后处理
 * 主要处理网络链接
 */
public class RpcBuildOnInit implements ApplicationListener<ContextRefreshedEvent>, DisposableBean, EnvironmentAware {

    private Environment environment;

    @Override
    public void destroy() throws Exception {
        ConcurrentHashMap<String, CopyOnWriteArrayList<InvokerChannel>> connectionManager =
                ClientConnectManger.getInstance().getConnectionManager();
        Collection<CopyOnWriteArrayList<InvokerChannel>> values = connectionManager.values();
        for (CopyOnWriteArrayList<InvokerChannel> ics : values) {
            for (InvokerChannel ic: ics) {
                ic.close();
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        String property = environment.getProperty(RpcConfig.zk_connect);
        CuratorClient client = new CuratorClient(property);
        //初始化
        ServiceDiscover serverDiscovers = new ServiceDiscover(client);
        try {
            serverDiscovers.findServer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcException("初始链接服务异常");
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
