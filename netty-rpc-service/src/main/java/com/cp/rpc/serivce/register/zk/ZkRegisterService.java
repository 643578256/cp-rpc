package com.cp.rpc.serivce.register.zk;

import com.cp.rpc.common.center.InterfaceInfo;
import com.cp.rpc.common.center.zk.CuratorClient;
import com.cp.rpc.common.ex.RpcException;
import com.cp.rpc.common.util.RpcConfig;
import com.cp.rpc.serivce.register.AbstractRegister;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.zookeeper.data.Stat;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ZkRegisterService extends AbstractRegister {

    private CuratorClient curatorClient;

    private Environment environment;

    private Lock lock = new ReentrantLock();



    public ZkRegisterService(List<Class> serverInterfase,Environment environment) {
        super(serverInterfase);
        this.environment = environment;
        String zk_connect = environment.getProperty(RpcConfig.zk_connect);
        curatorClient = new CuratorClient(zk_connect);
    }

    @Override
    public void registerService() throws Exception {
        lock.lock();
        try {
            //1.创建服务
            curatorClient.createRootService();
            String serverName = environment.getProperty(RpcConfig.service_name);
            if(StringUtils.isEmpty(serverName)){
                throw new RpcException("=========================rpc.cp.service.name not be null");
            }
            //serviceName#ip:port
            String ip = getLocalIp();
            String serverNameInfo = CuratorClient.DEFAULT_NAME_SPACE_P+"/"+serverName + "#" + ip +":" + RpcConfig.service_port_value;
            Stat stat = curatorClient.ckeckNodeExist(serverNameInfo);
            if (stat != null){
                logger.info("zk节点服务已存在。。。。。。。。。。");
            }else {
                String pathData = curatorClient.createPathData(serverNameInfo, new byte[]{});
                logger.info("创建服务============================节点={}",pathData);
            }

            //2.注册服务接口
            List<InterfaceInfo> collect = serverInterfase.stream().map(c -> new InterfaceInfo(c.getName(),"1") ).collect(Collectors.toList());
            ObjectMapper objectMapper = new ObjectMapper();
            String interfaNames = objectMapper.writeValueAsString(collect);

            String  serverNamePath = CuratorClient.DEFAULT_NAME_SPACE_S + "/" + serverName;
            Stat serverNamePathStat = curatorClient.ckeckNodeExist(serverNamePath);
            if (serverNamePathStat != null){
                logger.info("zk节点serverNamePathStat服务已存在。。。。。。。。。。");
            }else {
                String serverPathData = curatorClient.createPathData(serverNamePath,
                        interfaNames.getBytes(UTF_8));
                logger.info("注册服务接口serverNamePath============================节点={}",serverNamePath);
            }

        } finally {
            lock.unlock();
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
