package com.cp.rpc.client.invoker;

public interface Invoker {

    public Object doInvoker() throws Exception;

    public void setInvoker(Object returnObj) throws Exception;
}
