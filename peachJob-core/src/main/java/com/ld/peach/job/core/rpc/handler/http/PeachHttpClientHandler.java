package com.ld.peach.job.core.rpc.handler.http;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName PeachHttpClientHandler
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
public class PeachHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(PeachHttpClientHandler.class);

    private final PeachRpcInvokerFactory rpcInvokerFactory;
    private final IPeachJobRpcSerializer serializer;

    public PeachHttpClientHandler(final PeachRpcInvokerFactory rpcInvokerFactory, IPeachJobRpcSerializer serializer) {
        this.rpcInvokerFactory = rpcInvokerFactory;
        this.serializer = serializer;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {

        // response parse
        byte[] responseBytes = ByteBufUtil.getBytes(msg.content());

        // valid
        if (responseBytes.length == 0) {
            throw new PeachRpcException("peach rpc request data empty.");
        }

        // response deserialize
        PeachRpcResponse rpcResponse = (PeachRpcResponse) serializer.deserialize(responseBytes, PeachRpcResponse.class);

        // notify response
        rpcInvokerFactory.notifyInvokerFuture(rpcResponse.getRequestId(), rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        logger.error("peach rpc netty_http client caught exception", cause);
        ctx.close();
    }

    /*@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // retry
        super.channelInactive(ctx);
    }*/

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();      // close idle channel
            logger.debug("peach rpc netty_http client close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
