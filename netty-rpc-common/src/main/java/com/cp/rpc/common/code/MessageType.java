package com.cp.rpc.common.code;

public enum MessageType {

    REQUEST((byte)1,"BizRequestHandler"),
    RESPONSE((byte)2,"DefaultRequestHandler"),
    PING((byte)3,"DefaultRequestHandler"),
    PONG((byte)4,"DefaultRequestHandler");
    private byte type;

    private String handlerBeanName;

    MessageType(byte type, String handlerBeanName) {
        this.type = type;
        this.handlerBeanName = handlerBeanName;
    }

    public static MessageType messageType(byte type){
        MessageType[] values = MessageType.values();
        for (int i = 0; i < values.length; i++) {
            if(type == values[i].type){
                return values[i];
            }
        }
        return null;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getHandlerBeanName() {
        return handlerBeanName;
    }

    public void setHandlerBeanName(String handlerBeanName) {
        this.handlerBeanName = handlerBeanName;
    }
}
