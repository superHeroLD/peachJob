package com.ld.peach.job.core.rpc.server;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.Server;
import com.ld.peach.job.core.rpc.coder.DefaultDecoder;
import com.ld.peach.job.core.rpc.coder.DefaultEncoder;
import com.ld.peach.job.core.rpc.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PeachRpcServer
 * @Description RPC 服务器
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
@Slf4j
public class PeachRpcServer extends Server {

    private Thread thread;

    @Override
    public void start(final RpcProviderFactory rpcProviderFactory) throws Exception {
        thread = new Thread(() -> {

            //create thread pool
            final ThreadPoolExecutor serverHandlerThreadPool = new ThreadPoolExecutor(
                    60,
                    300,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    r -> new Thread(r, "peach-rpc-serverHandler-ThreadPool-" + r.hashCode()),
                    (r, executor) -> {
                        throw new PeachRpcException("peach-rpc-serverHandler-ThreadPool is EXHAUSTED!");
                    });

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                // start server
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                                        .addLast(new DefaultDecoder(PeachRpcRequest.class, rpcProviderFactory.getSerializer()))
                                        .addLast(new DefaultEncoder(PeachRpcResponse.class, rpcProviderFactory.getSerializer()))
                                        .addLast(new RpcServerHandler(rpcProviderFactory, serverHandlerThreadPool));
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(rpcProviderFactory.getPort()).sync();

                log.info("peach-job rpc remoting server start success, port: [{}]", rpcProviderFactory.getPort());

                onStarted();

                // wait util stop
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    log.info("peach-rpc remoting server stop.");
                } else {
                    log.error("peach-job rpc remoting server error.", e);
                }
            } finally {
                // stop
                try {
                    serverHandlerThreadPool.shutdown();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        onStopped();
        log.info("peach-rpc remoting server destroy success.");
    }
}
