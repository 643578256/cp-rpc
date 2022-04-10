package com.cp.rpc.common.router;


import cn.hutool.core.util.RandomUtil;
import com.cp.rpc.common.annotaion.SPI;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.ex.RpcException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@SPI(name = "weightRouter")
public class WeightRouter  /*extends AbstratRouter*/ implements IRouter  {
   /* public WeightRouter(String name) {
        super(name);
    }*/

    @Override
    public InvokerChannel chooseService(List<InvokerChannel> channels) {
        List<InvokerChannel> list = new ArrayList<>(channels);
        int sum = list.stream().mapToInt(InvokerChannel::getWeight).sum();
        List<InvokerChannel> sumList = new ArrayList<>(sum);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getWeight(); j++) {
                sumList.add(list.get(i));
            }
        }
        if(CollectionUtils.isEmpty(channels)){
            throw new RpcException("not find server ");
        }
        InvokerChannel invokerChannel = sumList.get(RandomUtil.randomInt(sum));
        list = null;
        sumList = null;
        return invokerChannel;
    }
}
