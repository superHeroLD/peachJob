package com.ld.peach.job.core.rpc.handler.http;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.exception.helper.ExceptionHelper;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName HttpServerHandler
 * @Description Http Server Handler
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final RpcProviderFactory rpcProviderFactory;
    private final ThreadPoolExecutor serverHandlerPool;

    public HttpServerHandler(final RpcProviderFactory rpcProviderFactory, final ThreadPoolExecutor serverHandlerPool) {
        this.rpcProviderFactory = rpcProviderFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        // request parse
        final byte[] requestBytes = ByteBufUtil.getBytes(msg.content());
        final String uri = msg.uri();
        final boolean keepAlive = HttpUtil.isKeepAlive(msg);

        // do invoke
        serverHandlerPool.execute(() -> process(ctx, uri, requestBytes, keepAlive));
    }

    private void process(ChannelHandlerContext ctx, String uri, byte[] requestBytes, boolean keepAlive) {
        String requestId = null;
        try {
            // services mapping
            if ("/services".equals(uri)) {
                // request
                StringBuilder stringBuffer = new StringBuilder("<ui>");
                for (String serviceKey : rpcProviderFactory.getServiceData().keySet()) {
                    stringBuffer.append("<li>").append(serviceKey).append(": ").append(rpcProviderFactory.getServiceData().get(serviceKey)).append("</li>");
                }
                stringBuffer.append("</ui>");

                // response serialize
                byte[] responseBytes = stringBuffer.toString().getBytes(StandardCharsets.UTF_8);

                // response-write
                writeResponse(ctx, keepAlive, responseBytes);

            } else {
                // valid
                if (requestBytes.length == 0) {
                    throw new PeachRpcException("peach rpc request data empty.");
                }

                // request deserialize
                PeachRpcRequest rpcRequest = (PeachRpcRequest) rpcProviderFactory.getSerializer().deserialize(requestBytes, PeachRpcRequest.class);
                requestId = rpcRequest.getRequestId();

                // invoke + response
                PeachRpcResponse rpcResponse = rpcProviderFactory.invokeService(rpcRequest);

                // response serialize
                byte[] responseBytes = rpcProviderFactory.getSerializer().serialize(rpcResponse);

                // response-write
                writeResponse(ctx, keepAlive, responseBytes);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            // response error
            PeachRpcResponse response = new PeachRpcResponse();
            response.setRequestId(requestId);
            response.setErrorMsg(ExceptionHelper.getErrorInfo(e));

            // response serialize
            byte[] responseBytes = rpcProviderFactory.getSerializer().serialize(response);

            // response-write
            writeResponse(ctx, keepAlive, responseBytes);
        }
    }

    /**
     * write response
     */
    private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, byte[] responseBytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("peach rpc provider netty_http server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // close idle channel
            ctx.channel().close();
            log.info("peach-rpc provider netty_http server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
