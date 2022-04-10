package com.cp.rpc.common.router;

import cn.hutool.core.util.RandomUtil;
import com.cp.rpc.common.annotaion.SPI;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@SPI(name = "random")
public class RandomRouter /* extends AbstratRouter*/ implements IRouter  {

    /*public RandomRouter(String name) {
        super(name);
    }*/

    @Override
    public InvokerChannel chooseService(List<InvokerChannel> channels) {
        List<InvokerChannel> list = new ArrayList<>(channels);
        if(CollectionUtils.isEmpty(list)){
            throw new RpcException("not find server ");
        }
        InvokerChannel invokerChannel = list.get(RandomUtil.randomInt(list.size()));
        list = null;
        return invokerChannel;
    }
}
