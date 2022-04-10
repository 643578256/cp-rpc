package com.cp.rpc.common.center.zk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.List;

/**
 *
 * /rpc-cp/service    服务端信息
 * /rpc-cp/providers  服务接口信息
 */
public class CuratorClient extends AbstractOption {
    private CuratorFramework zkClient;
    private final static String DEFAULT_NAME_SPACE = "rpc-cp";
    public final static String DEFAULT_SPLIT = "#"; //serviceName#ip:port
    public final static String DEFAULT_NAME_SPACE_P = "/providers"; // /providers/服务的名字#ip:port
    public final static String DEFAULT_NAME_SPACE_S = "/service";  // /server/(服务的名字)

    final static int d_sessionTimeout = 20000  * 3; //建议会话时间1分钟到3分钟 ，且是连接的3倍 sofa 就是这个配置默认
    final static int d_connectionTimeout = 20000 ;

    //ip:port,ip:port
    public CuratorClient(String connectStr) {
        this(connectStr, DEFAULT_NAME_SPACE, d_sessionTimeout, d_connectionTimeout);
    }

    private CuratorClient(String connectStr, int sessionTimeout, int connectionTimeout) {
        this(connectStr, DEFAULT_NAME_SPACE, sessionTimeout, connectionTimeout);
    }

    public CuratorClient(String connectStr, String namespace, int sessionTimeout, int connectionTimeout) {
        zkClient = CuratorFrameworkFactory.builder()
                .namespace(namespace)
                .connectString(connectStr)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        zkClient.start();
    }

    public CuratorFramework getClient() {
        return this.zkClient;
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        zkClient.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public String createPathData(String path, byte[] data) throws Exception {
        return zkClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, data);
    }

    public String createRootService() throws Exception {
        Stat stat = zkClient.checkExists().forPath(DEFAULT_NAME_SPACE_P);
        if(stat != null){
            return stat.toString();
        }
        return zkClient.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(DEFAULT_NAME_SPACE_P);
    }


    public void updatePathData(String path, byte[] data) throws Exception {
        zkClient.setData().forPath(path, data);
    }

    public void deletePath(String path) throws Exception {
        zkClient.delete().forPath(path);
    }

    public void watchNode(String path, Watcher watcher) throws Exception {
        zkClient.getData().usingWatcher(watcher).forPath(path);
    }

    public byte[] getData(String path) throws Exception {
        return zkClient.getData().forPath(path);
    }

    public <T> T getDataForObject(String path, Class zclass) throws Exception {
        byte[] bytes = zkClient.getData().forPath(path);
        String s = new String(bytes, Charset.forName("UTF-8"));
        ObjectMapper objectMapper = new ObjectMapper();
        Object zObj = objectMapper.readValue(s, zclass);
        return (T) zObj;
    }

    public List<String> getChildren(String path) throws Exception {
        return zkClient.getChildren().forPath(path);
    }

    public void watchTreeNode(String path, TreeCacheListener listener) {
        TreeCache treeCache = new TreeCache(zkClient, path);
        treeCache.getListenable().addListener(listener);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        //BUILD_INITIAL_CACHE 代表使用同步的方式进行缓存初始化。
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        pathChildrenCache.getListenable().addListener(listener);
    }

    public Stat ckeckNodeExist(String nodePath) throws Exception {
        return zkClient.checkExists().forPath(nodePath);
    }

    public void close() {
        zkClient.close();
    }
}

