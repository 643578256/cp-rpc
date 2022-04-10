package com.cp.rpc.common.code;


import com.cp.rpc.common.code.MessageType;
import com.cp.rpc.common.code.ProtocolConstant;
import com.cp.rpc.common.code.RpcRequestMessageData;
import com.cp.rpc.common.code.seri.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
public class  ByteToMessageDecoderHandler extends ByteToMessageDecoder {

    Logger logger = LoggerFactory.getLogger(ByteToMessageDecoderHandler.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        MDC.put("traceId", UUID.randomUUID().toString());
        int readableBytes = in.readableBytes();
        if(readableBytes < ProtocolConstant.minLen){
            logger.info("ProtocolConstant.minLenProtocolConstant.minLenProtocolConstant.minLen");
            return;
        }
        in.markReaderIndex();
        byte b = in.readByte();
        if(b != ProtocolConstant.magic){
            in.resetReaderIndex();
            logger.info("ProtocolConstant.magicProtocolConstant.magicProtocolConstant.magic");
            return;
        }
        byte version = in.readByte();
        long sessionId = in.readLong();
        byte type = in.readByte();
        MessageType messageType = MessageType.messageType(type);
        if(messageType.getType() == MessageType.PING.getType()){
            logger.info("当前消息是心跳消息");
            RpcRequestMessageData heard = new RpcRequestMessageData();
            heard.setRequestId(in.readInt());
            out.add(heard);
            in.clear();
            /*byte[] b =new byte[11];
            in.readBytes();*/
            return;
        }
        int len = in.readInt();
        readableBytes = in.readableBytes();
        if(len > readableBytes){
            in.resetReaderIndex();
            return;
        }
        byte[] contents = new byte[len];
        in.readBytes(contents);
        Serializable requestObj = HessianSerializer.SERIALIZER.deSerializer(contents);
        /*if(messageType.getType() == MessageType.RESPONSE.getType()){

        }else if(messageType.getType() == MessageType.RESPONSE.getType()){

        }*/
        out.add(requestObj);
        //这里配合，标记读位置和重置读位置
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
