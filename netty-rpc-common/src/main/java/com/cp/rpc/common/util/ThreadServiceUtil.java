package com.cp.rpc.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadServiceUtil {

    /**
     * 只能客户端网络重连
     */
    public static ScheduledExecutorService SCHEDULED = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2,new NamedThreadFactory("client-reconnect-t"));

}
