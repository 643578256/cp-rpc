package com.cp.rpc.serivce.register;

import com.cp.rpc.common.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractRegister {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port = 9877;

    private int contReadTimeOut;

    private int contWriteTimeOut;

    protected List<Class> serverInterfase;

    public AbstractRegister(List<Class> serverInterfase) {
        this.serverInterfase = serverInterfase;
    }

    protected static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("rpc-server-reRegister-t"));

    public abstract void registerService() throws Exception;
}
