package com.cp.rpc.client.invoker;

import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.common.code.RpcProtocolData;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class AbstarctInvker implements Invoker {

    private InvokerChannel invokerChannel;

    private CountDownLatch countDownLatch;

    private Object returnObj;

    private int readTimeOut = 30; //默认读取时间

    public AbstarctInvker(InvokerChannel invokerChannel, CountDownLatch countDownLatch) {
        this.invokerChannel = invokerChannel;
        this.countDownLatch = countDownLatch;
    }

    protected abstract Serializable buildRequestData();

    protected abstract void doAfterInvoker();

    protected abstract void sendedSoming();

    @Override
    public Object doInvoker() throws Exception {
        Serializable metaData = buildRequestData();

        invokerChannel.getChannel().writeAndFlush(metaData).addListener(new GenericFutureListener(){
            @Override
            public void operationComplete(Future future) throws Exception {
                if(future.isSuccess()){
                    sendedSoming();
                }
            }
        });
        if(metaData instanceof RpcProtocolData){
            RpcProtocolData rpcProtocolData = (RpcProtocolData) metaData;
            long sessionId = rpcProtocolData.getSessionId();
            ClientConnectManger.getInstance().putRequestInfo(sessionId,this);
        }else {
            throw new UnsupportedOperationException("不支持此序列化数据");
        }
        countDownLatch.await(readTimeOut, TimeUnit.SECONDS);
        if(returnObj == null){
            throw new RpcException("time out");
        }
        doAfterInvoker();
        return returnObj;
    }

    //回填数据
    @Override
    public void setInvoker(Object returnObj) throws Exception {
        this.returnObj = returnObj;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
