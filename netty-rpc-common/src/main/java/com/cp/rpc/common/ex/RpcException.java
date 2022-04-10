package com.cp.rpc.common.ex;

public class RpcException extends RuntimeException {
    private Object object;
    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    public RpcException(String message, Object object) {
        super(message);
        this.object = object;
    }
}
