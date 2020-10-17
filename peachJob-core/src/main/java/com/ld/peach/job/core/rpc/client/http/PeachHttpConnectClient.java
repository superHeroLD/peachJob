package com.ld.peach.job.core.rpc.client.http;

import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.rpc.common.ConnectClient;
import com.ld.peach.job.core.rpc.handler.http.PeachHttpClientHandler;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PeachHttpConnectClient
 * @Description peach http connect client
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
public class PeachHttpConnectClient extends ConnectClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeachHttpConnectClient.class);

    private EventLoopGroup group;
    private Channel channel;

    private IPeachJobRpcSerializer serializer;
    private String address;
    private String host;

    @Override
    public void init(String address, final IPeachJobRpcSerializer serializer, final PeachRpcInvokerFactory rpcInvokerFactory) throws Exception {

        if (!address.toLowerCase().startsWith("http")) {
            address = "http://" + address;
        }

        this.address = address;
        URL url = new URL(address);
        this.host = url.getHost();
        int port = url.getPort() > -1 ? url.getPort() : 80;

        this.group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.MINUTES))
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                .addLast(new PeachHttpClientHandler(rpcInvokerFactory, serializer));
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);

        this.channel = bootstrap.connect(host, port).sync().channel();

        this.serializer = serializer;

        // valid
        if (!isValidate()) {
            close();
            return;
        }

        LOGGER.debug("peach rpc netty http client proxy, connect to server success at host:{}, port:{}", host, port);
    }


    @Override
    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }


    @Override
    public void close() {
        if (this.channel != null && this.channel.isActive()) {
            // if this.channel.isOpen()
            this.channel.close();
        }
        if (this.group != null && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }

        LOGGER.debug("peach rpc netty http client close.");
    }


    @Override
    public void send(PeachRpcRequest rpcRequest) throws Exception {
        byte[] requestBytes = serializer.serialize(rpcRequest);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, new URI(address).getRawPath(), Unpooled.wrappedBuffer(requestBytes));
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

        this.channel.writeAndFlush(request).sync();
    }
}
