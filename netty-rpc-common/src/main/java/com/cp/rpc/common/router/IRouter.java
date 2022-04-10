package com.cp.rpc.common.router;

import cn.hutool.core.util.RandomUtil;
import com.cp.rpc.common.annotaion.SPI;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import com.cp.rpc.common.util.ThreadServiceUtil;

import java.util.List;


public interface IRouter {

    InvokerChannel chooseService(List<InvokerChannel> channels);


    default InvokerChannel doChooseService(List<InvokerChannel> channels){
        InvokerChannel invokerChannel = chooseService(channels);
        if(!invokerChannel.checkActive()){
            //ThreadServiceUtil.SCHEDULED.schedule()
            channels.remove(invokerChannel);
            int size = channels.size();
            if(size == 0){
                throw new RpcException("没有可用的服务",invokerChannel);
            }
            return channels.get(RandomUtil.randomInt(size));
        }
        return invokerChannel;
    }

}
