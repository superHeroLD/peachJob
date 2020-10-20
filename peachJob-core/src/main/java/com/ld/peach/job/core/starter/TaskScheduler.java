package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.handler.servlet.ServletServerHandler;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import com.ld.peach.job.core.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TaskScheduler
 * @Description 定时执行任务
 * @Author lidong
 * @Date 2020/10/20
 * @Version 1.0
 */
@Slf4j
@Configuration
public class TaskScheduler implements InitializingBean, DisposableBean {

    private ScheduledExecutorService executor;

    private RpcProviderFactory rpcProviderFactory;

    private static ServletServerHandler servletServerHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        initRpcProvider();

        executor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            final AtomicInteger atomicInteger = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("[peach-admin]-scheduled-thread[" + atomicInteger.getAndIncrement() + "]");
                return thread;
            }
        });

        log.info("[TaskScheduler] init admin service success.");
    }

    @Override
    public void destroy() throws Exception {
        // stop-schedule
        if (Objects.nonNull(executor)) {
            executor.shutdown();
        }

        // stop rpc
        stopRpcProvider();
    }

    private void stopRpcProvider() throws Exception {
        rpcProviderFactory.stop();
    }

    /**
     * 初始化RPC
     */
    private void initRpcProvider() {
        // init
        this.rpcProviderFactory = new RpcProviderFactory();
        rpcProviderFactory.initConfig(
                new HessianSerializer(),
                null,
                0,
                "peach job",
                null,
                null);

        // TODO 这里要写注册逻辑了
//        rpcProviderFactory.addService(ITaskService.class.getName(), null, null);

        // servlet handler
        servletServerHandler = new ServletServerHandler(this.rpcProviderFactory);
    }

    public static void invokeAdminService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        servletServerHandler.handle(null, request, response);
    }
}
