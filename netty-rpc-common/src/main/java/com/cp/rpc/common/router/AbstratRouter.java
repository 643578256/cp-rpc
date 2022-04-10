package com.cp.rpc.common.router;

import cn.hutool.core.util.RandomUtil;
import com.cp.rpc.common.core.InvokerChannel;

import java.util.List;

public abstract class AbstratRouter {

    /*protected boolean checkChannleActive(InvokerChannel invokerChannel){
        invokerChannel.checkActive();
    }*/

    private String name;
    public AbstratRouter(String name){
        this.name =name;
    }

    public String getName(){
        return name;
    }


    protected abstract InvokerChannel chooseService(List<InvokerChannel> channels);

    public InvokerChannel doChooseService(List<InvokerChannel> channels){
        InvokerChannel invokerChannel = chooseService(channels);
        if(!invokerChannel.checkActive()){
            channels.remove(invokerChannel);
            int size = channels.size();
            return channels.get(RandomUtil.randomInt(size));
        }
        return invokerChannel;
    }

}
