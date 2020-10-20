package com.ld.peach.job.core.rpc.server;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.Server;
import com.ld.peach.job.core.rpc.handler.http.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PeachHttpServer
 * @Description Http 服务器
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
@Slf4j
public class PeachHttpServer extends Server {

    private Thread thread;

    @Override
    public void start(RpcProviderFactory rpcProviderFactory) throws Exception {

        thread = new Thread(() -> {
            final ThreadPoolExecutor serverHandlerThreadPool = new ThreadPoolExecutor(
                    60,
                    300,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    r -> new Thread(r, "peach-rpc-httpServer-ThreadPool-" + r.hashCode()),
                    (r, executor) -> {
                        throw new PeachRpcException("peach-rpc-httpServer-ThreadPool is EXHAUSTED!");
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
                                        .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.MINUTES))
                                        .addLast(new HttpServerCodec())
                                        .addLast(new HttpObjectAggregator(5 * 1024 * 1024))
                                        .addLast(new HttpServerHandler(rpcProviderFactory, serverHandlerThreadPool));
                            }
                        }).childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(rpcProviderFactory.getPort()).sync();

                log.info("peach-rpc remoting server start success, nettype = {}, port = {}", PeachHttpServer.class.getName(), rpcProviderFactory.getPort());
                onStarted();

                // wait util stop
                future.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                log.info("peach-rpc remoting server stop.");
            } finally {
                // stop
                try {
                    // shutdownNow
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

        // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        // destroy server thread
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        // on stop
        onStopped();
        log.info("peach-rpc remoting http server destroy success.");
    }
}
