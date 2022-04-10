package com.cp.rpc.common.code.seri;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.cp.rpc.common.code.RpcProtocolData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {

    private SerializerFactory serializerFactory = new SerializerFactory();

    public static final HessianSerializer SERIALIZER = new HessianSerializer();

    private HessianSerializer(){}

    @Override
    public <T> T deSerializer(byte[] content) throws Exception{
        ByteArrayInputStream byteArray = new ByteArrayInputStream(content);
        Hessian2Input output = new Hessian2Input(byteArray);
        output.setSerializerFactory(serializerFactory);
        T object = null;
        try {
            object  = (T)output.readObject();
            output.close();
        } catch (IOException e) {
            throw new Exception("序列化异常", e);
        }
        return object;
    }

    @Override
    public byte[] enSerializer(Object serObj) throws Exception {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(serObj);
            output.close();
        } catch (IOException e) {
            throw new Exception("序列化异常", e);
        }

        return byteArray.toByteArray();
    }
}
