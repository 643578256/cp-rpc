package com.cp.rpc.common.center.zk;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;

abstract  class AbstractOption {

    public abstract  <T> T getClient();

    public abstract void addConnectionStateListener(ConnectionStateListener connectionStateListener);

    public abstract String createPathData(String path, byte[] data) throws Exception;


    public abstract void updatePathData(String path, byte[] data) throws Exception;

    public abstract void deletePath(String path) throws Exception;

    public abstract void watchNode(String path, Watcher watcher) throws Exception;

    public abstract byte[] getData(String path) throws Exception;

    public abstract <T> T getDataForObject(String path,Class zclass) throws Exception;

    public abstract List<String> getChildren(String path) throws Exception;

    public abstract void watchTreeNode(String path, TreeCacheListener listener);

    public abstract void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception;

    public abstract void close();
}