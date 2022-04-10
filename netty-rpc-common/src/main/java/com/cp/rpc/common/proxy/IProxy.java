package com.cp.rpc.common.proxy;

public interface IProxy {

    /**
     * 获取服务代理对象
     * @param <T>
     * @return
     */
     <T> T getProxy(Class zclass);

    /**
     * 获取服务对象
     * @param <T>
     * @return
     */
     <T> T getRef(Class zclass, Object proxy);

}
