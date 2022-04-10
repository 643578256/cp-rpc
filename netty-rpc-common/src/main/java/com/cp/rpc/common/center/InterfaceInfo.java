package com.cp.rpc.common.center;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 可以留有扩展
 */
public class InterfaceInfo implements Serializable {
    private String interfaceName;
    private String version = "1";


    public InterfaceInfo() {
    }

    public InterfaceInfo(String interfaceName, String version) {
        this.interfaceName = interfaceName;
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        InterfaceInfo info = (InterfaceInfo) obj;
        if(StringUtils.isEmpty(info.getInterfaceName()) || StringUtils.isEmpty(info.getVersion())){
            return false;
        }
        if(info.getInterfaceName().equals(interfaceName) && info.getVersion().equals(version)){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "InterfaceInfo{" +
                "interfaceName='" + interfaceName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
