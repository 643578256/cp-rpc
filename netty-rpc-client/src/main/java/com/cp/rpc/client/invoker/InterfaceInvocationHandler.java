package com.cp.rpc.client.invoker;

import com.cp.rpc.client.net.ClientConnectManger;
import com.cp.rpc.common.core.InvokerChannel;
import com.cp.rpc.common.exten.Extentions;
import com.cp.rpc.common.router.IRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class InterfaceInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(InterfaceInvocationHandler.class);
    private String interfaceName;
    private IRouter router;



    public InterfaceInvocationHandler(String interfaceName) {
        this.interfaceName = interfaceName;
        this.router = Extentions.loadExtation(IRouter.class,"random");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        logger.info("================invoke=========={}",interfaceName);
        List<InvokerChannel> invokerChannels = ClientConnectManger.getInstance().getInvokerChannels(interfaceName);
        InvokerChannel invokerChannel = router.doChooseService(invokerChannels);
        if (invokerChannel.checkActive()) {
            Invoker invoker = new ClintInvoker(invokerChannel, method, interfaceName, args);
            return invoker.doInvoker();

        }
        //抛出异常
        return null;
    }
}
