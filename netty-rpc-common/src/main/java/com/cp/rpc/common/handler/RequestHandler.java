package com.cp.rpc.common.handler;


import java.lang.reflect.InvocationTargetException;

public interface RequestHandler extends Runnable {

    void executeHandler() ;

}
