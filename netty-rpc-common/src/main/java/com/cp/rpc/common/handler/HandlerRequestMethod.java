package com.cp.rpc.common.handler;

import java.lang.reflect.Method;

public class HandlerRequestMethod {
    private Object ref;
    private Class realClass;
    private Method method;
    private String methodName;
    private Class[] paramClass;
    private int version = 1;

    public HandlerRequestMethod(Object object, Class realClass, Method method) {
        this.ref = object;
        this.realClass = realClass;
        this.method = method;
        this.methodName = method.getName();
        this.paramClass = method.getParameterTypes();
    }

    public Class getRealClass() {
        return realClass;
    }

    public void setRealClass(Class realClass) {
        this.realClass = realClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class[] getParamClass() {
        return paramClass;
    }

    public void setParamClass(Class[] paramClass) {
        this.paramClass = paramClass;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
