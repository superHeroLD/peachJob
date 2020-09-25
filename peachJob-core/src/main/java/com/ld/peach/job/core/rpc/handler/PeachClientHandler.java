package com.ld.peach.job.core.rpc.handler;

import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName PeachClientHandler
 * @Description RPC Client Handler
 * @Author lidong
 * @Date 2020/9/25
 * @Version 1.0
 */
public class PeachClientHandler extends SimpleChannelInboundHandler<PeachRpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeachClientHandler.class);

    private final PeachRpcInvokerFactory rpcInvokerFactory;

    public PeachClientHandler(final PeachRpcInvokerFactory rpcInvokerFactory) {
        this.rpcInvokerFactory = rpcInvokerFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PeachRpcResponse response) throws Exception {
        rpcInvokerFactory.notifyInvokerFuture(response.getRequestId(), response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("peach rpc netty client caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            LOGGER.debug("peach rpc netty client close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
