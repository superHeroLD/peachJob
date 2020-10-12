package com.ld.peach.job.core.rpc.client;

import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.coder.DefaultDecoder;
import com.ld.peach.job.core.rpc.coder.DefaultEncoder;
import com.ld.peach.job.core.rpc.common.ConnectClient;
import com.ld.peach.job.core.rpc.handler.PeachClientHandler;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PeachConnectClient
 * @Description netty client
 * @Author lidong
 * @Date 2020/9/25
 * @Version 1.0
 */
public class PeachConnectClient extends ConnectClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeachConnectClient.class);

    private EventLoopGroup group;
    private Channel channel;

    @Override
    public void init(String address, final IPeachJobRpcSerializer serializer, final PeachRpcInvokerFactory rpcInvokerFactory) throws Exception {
        if (!address.toLowerCase().startsWith("http")) {
            address = "http://" + address;
        }

        URL url = new URL(address);
        String host = url.getHost();
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
                                .addLast(new DefaultEncoder(PeachRpcRequest.class, serializer))
                                .addLast(new DefaultDecoder(PeachRpcResponse.class, serializer))
                                .addLast(new PeachClientHandler(rpcInvokerFactory));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.channel = bootstrap.connect(host, port).sync().channel();

        if (!isValidate()) {
            close();
            return;
        }

        LOGGER.debug("Jobs rpc netty client proxy, connect to server success at host:{}, port:{}", host, port);
    }


    @Override
    public boolean isValidate() {
        if (Objects.nonNull(this.channel)) {
            return this.channel.isActive();
        }
        return false;
    }

    @Override
    public void close() {
        if (Objects.nonNull(this.channel) && this.channel.isActive()) {
            this.channel.close();
        }

        if (Objects.nonNull(this.group) && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }

        LOGGER.debug("Jobs rpc netty client close.");
    }


    @Override
    public void send(PeachRpcRequest rpcRequest) throws Exception {
        this.channel.writeAndFlush(rpcRequest).sync();
    }
}
