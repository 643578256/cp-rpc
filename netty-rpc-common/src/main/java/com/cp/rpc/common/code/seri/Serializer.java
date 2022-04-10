package com.cp.rpc.common.code.seri;

import com.cp.rpc.common.code.RpcProtocolData;

public interface Serializer {
    /**
     * 反序列化
     * @param content
     * @param <T>
     * @return
     */
    <T> T deSerializer(byte[] content) throws Exception;


    /**
     * 反序列化
     * @param serObj
     * @param <T>
     * @return
     */
    <T> T enSerializer(Object serObj) throws Exception;
}
