package com.cp.rpc.common.router;


import com.cp.rpc.common.annotaion.SPI;
import com.cp.rpc.common.core.InvokerChannel;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;


@SPI(name = "lurRouter")
public class LurRouter /*extends AbstratRouter*/ implements IRouter {


   /* public LurRouter(String name) {
        super(name);
    }*/

    @Override
    public InvokerChannel chooseService(List<InvokerChannel> channels) {
        TreeMap<Long,InvokerChannel> treeMap = new TreeMap();
        channels.forEach(c -> {
            treeMap.put(c.getClickTimes(),c);
        });
        //按key 升序所以 默认取第一个
        InvokerChannel value = treeMap.firstEntry().getValue();
        treeMap.clear();
        return value;
    }


}
