package com.cp.rpc.client.discover;


import java.util.List;
import com.cp.rpc.common.center.zk.HostAndPort;

public abstract class AbstarctServerDiscovers {

    abstract List<HostAndPort> findServer() throws Exception;
}
