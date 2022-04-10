package com.cp.rpc.common.code;


import java.io.Serializable;

/**
 * +--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+
 * |  BYTE  |        |        |        |        |        |        |             ........
 * +--------------------------------------------+--------+-----------------+--------+--------+--------+--------+--------+--------+-----------------+
 * |  magic | version| sessionId| messageType|type  |           content length           |                   content byte[]                                        |        |
 * +--------+-----------------------------------------------------------------------------------------+--------------------------------------------+
 * min = 1 + 1 + 8 + 1+ 4 = 15
 */
public class RpcProtocolData implements Serializable {

    private byte magic = 3;
    private byte version =1;
    private long sessionId;
    private byte messageType; //1 表示请求，2 表示相应 3 表示ping 4 表示pong
    private int length;
    private byte[] content;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getMagic() {
        return magic;
    }

    public void setMagic(byte magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
