package com.cp.rpc.common.center.zk;


import org.springframework.util.StringUtils;

import java.io.Serializable;

public class HostAndPort implements Serializable {

    private static final long serialVersionUID = 2757511711418608253L;
    private String host;
    private Integer port;

    public HostAndPort(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        HostAndPort hp = ((HostAndPort) o);
        if(StringUtils.pathEquals(hp.getHost(),host) && port.intValue() == hp.getPort().intValue()){
           return true;
       }
       return false;
    }

    @Override
    public String toString() {
        return "HostAndPort{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
