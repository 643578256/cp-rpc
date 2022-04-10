package com.cp.rpc.common.code;

import com.cp.rpc.common.code.RpcProtocolData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码消息，可共用一个handler
 * +--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+--------+
 * |  BYTE  |        |        |        |        |        |        |             ........
 * +--------------------------------------------+--------+-----------------+--------+--------+--------+--------+--------+--------+-----------------+
 * |  magic | version| sessionId| messageType  |  content length        |    content byte[]                                        |        |
 * +--------+-----------------------------------------------------------------------------------------+--------------------------------------------+
 * min = 1 + 1 + 8 + 1+ 4  +len= 15+len
 */
//@ChannelHandler.Sharable
public class MessageToByteEncodingHandler extends MessageToByteEncoder<RpcProtocolData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocolData msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getMagic());
        out.writeByte(msg.getVersion());
        out.writeLong(msg.getSessionId());
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getLength());
        if(msg.getLength() == 0){
            out.writeByte(0);
        }else {
            out.writeBytes(msg.getContent());
        }
    }
}
