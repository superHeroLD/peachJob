package com.ld.peach.job.core.rpc.handler;

import com.ld.peach.job.core.exception.helper.ExceptionHelper;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName RpcServerHandler
 * @Description rpc server handler
 * @Author lidong
 * @Date 2020/9/17
 * @Version 1.0
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<PeachRpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private final RpcProviderFactory rpcProviderFactory;
    private final ThreadPoolExecutor serverHandlerPool;

    public RpcServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory = rpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PeachRpcRequest peachRpcRequest) throws Exception {
        try {
            // do invoke
            serverHandlerPool.execute(() -> {
                PeachRpcResponse xxlRpcResponse = rpcProviderFactory.invokeService(peachRpcRequest);

                ctx.writeAndFlush(xxlRpcResponse);
            });
        } catch (Exception e) {
            // catch error
            PeachRpcResponse jobsRpcResponse = new PeachRpcResponse();
            jobsRpcResponse.setRequestId(peachRpcRequest.getRequestId());
            jobsRpcResponse.setErrorMsg(ExceptionHelper.getErrorInfo(e));
            ctx.writeAndFlush(jobsRpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("peach-rpc provider netty server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            LOGGER.debug("peach-rpc provider netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
