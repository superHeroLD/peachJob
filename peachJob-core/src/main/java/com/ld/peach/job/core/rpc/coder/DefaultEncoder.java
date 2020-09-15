package com.ld.peach.job.core.rpc.coder;

import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName DefaultEncoder
 * @Description 默认 Encoder
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public class DefaultEncoder extends MessageToByteEncoder<Object> {

    private final Class<?> genericClass;
    private final IPeachJobRpcSerializer serializer;

    public DefaultEncoder(Class<?> genericClass, final IPeachJobRpcSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
