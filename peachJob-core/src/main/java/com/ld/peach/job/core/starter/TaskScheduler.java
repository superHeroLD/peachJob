package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.executor.ITaskExecutor;
import com.ld.peach.job.core.handler.servlet.ServletServerHandler;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.invoker.call.CallType;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import com.ld.peach.job.core.service.IAdminService;
import com.ld.peach.job.core.service.PeachJobHeartBeat;
import com.ld.peach.job.core.service.PeachJobHelper;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
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

        executor.scheduleAtFixedRate(new PeachJobHeartBeat(), 1, 1, TimeUnit.SECONDS);

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
        if (Objects.nonNull(rpcProviderFactory)) {
            rpcProviderFactory.stop();
        }
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
                PeachJobHelper.getJobsProperties().getAdminAccessToken(),
                null,
                null);

        //服务注册
        rpcProviderFactory.addService(IAdminService.class.getName(), null, PeachJobHelper.getAppService());

        servletServerHandler = new ServletServerHandler(this.rpcProviderFactory);
    }

    public static void invokeAdminService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        servletServerHandler.handle(request, response);
    }

    private static final Map<String, ITaskExecutor> TASK_EXECUTOR = new ConcurrentHashMap<>();

    public static ITaskExecutor getTaskExecutor(String address) {
        if (StringUtil.isBlank(address)) {
            return null;
        }

        // load-cache
        address = address.trim();
        ITaskExecutor taskExecutor = TASK_EXECUTOR.get(address);
        if (Objects.nonNull(taskExecutor)) {
            return taskExecutor;
        }

        // set-cache
        taskExecutor = (ITaskExecutor) new RpcReferenceBean(
                PeachJobHelper.getRpcSerializer(),
                CallType.SYNC,
                ITaskExecutor.class,
                null,
                5000,
                address,
                PeachJobHelper.getJobsProperties().getAppAccessToken(),
                null,
                null).getObject();

        TASK_EXECUTOR.put(address, taskExecutor);
        log.info("[TaskScheduler] put address: {} taskExecutor: {} to local cache", address, taskExecutor.getClass().getName());
        return taskExecutor;
    }
}
