package com.cp.rpc.common.code;

import java.io.Serializable;
import java.util.Arrays;

public class RpcRequestMessageData implements Serializable {
    private static final long serialVersionUID = -7540025652277135640L;
    private long requestId;
    private Integer version;
    private String interfaceName;
    private String method;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RpcMessageData{" +
                "requestId=" + requestId +
                ", version=" + version +
                ", interfaceName='" + interfaceName + '\'' +
                ", method='" + method + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
