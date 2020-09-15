package com.ld.peach.job.core.rpc.coder;

import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName DefaultDecoder
 * @Description 默认Decoder
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public class DefaultDecoder extends ByteToMessageDecoder {

    private final Class<?> genericClass;
    private final IPeachJobRpcSerializer serializer;

    public DefaultDecoder(Class<?> genericClass, final IPeachJobRpcSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = serializer.deserialize(data, genericClass);
        out.add(obj);
    }
}
