package com.cp.rpc.common.code;

import java.io.Serializable;
import java.util.Arrays;

public class RpcResponseMessageData implements Serializable {
    private static final long serialVersionUID = -8577697871990331491L;
    private long responseId;
    private Object responseObj;

    public long getResponseId() {
        return responseId;
    }

    public void setResponseId(long responseId) {
        this.responseId = responseId;
    }

    public Object getResponseObj() {
        return responseObj;
    }

    public void setResponseObj(Object responseObj) {
        this.responseObj = responseObj;
    }
}
