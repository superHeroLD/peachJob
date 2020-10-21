package com.ld.peach.job.core.rpc.registry;

import com.ld.peach.job.core.constant.RegisterStatusEnum;
import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.executor.AbstractTaskExecutor;
import com.ld.peach.job.core.model.params.RegistryParam;
import com.ld.peach.job.core.service.IAppService;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName ExecuteregistyThread
 * @Description 执行服务注册线程
 * @Author lidong
 * @Date 2020/10/12
 * @Version 1.0
 */
@Slf4j
public class ExecutorRegistryThread {

    private Thread registryThread;

    private volatile boolean toStop = false;

    /**
     * 单例
     */
    private static final ExecutorRegistryThread INSTANCE = new ExecutorRegistryThread();

    public static ExecutorRegistryThread getInstance() {
        return INSTANCE;
    }

    /**
     * 启动服务注册线程
     *
     * @param appName 服务名称-写在配置文件里
     * @param address 服务地址-IP：端口
     */
    public void start(final String appName, final String address) {
        if (StringUtil.isBlank(appName)) {
            log.warn("[ExecutorRegistryThread] app is null");
            return;
        }

        if (StringUtil.isBlank(address)) {
            log.warn("[ExecutorRegistryThread] address is null");
            return;
        }

        registryThread = new Thread(() -> {
            //不断的轮训注册
            while (!toStop) {
                try {
                    RegistryParam registryParam = new RegistryParam(appName, address);
                    for (IAppService taskService : AbstractTaskExecutor.getTaskServiceList()) {
                        try {
                            if (taskService.registry(registryParam)) {
                                log.info("[ExecutorRegistryThread] Task registry success, registryParam: {}", registryParam);
                                break;
                            } else {
                                log.info("[ExecutorRegistryThread] Task registry fail, registryParam: {}", registryParam);
                            }
                        } catch (Exception e) {
                            log.info("[ExecutorRegistryThread] Task registry error, registryParam: {}", registryParam, e);
                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }

                try {
                    if (!toStop) {
                        TimeUnit.SECONDS.sleep(TaskConstant.BEAT_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    if (!toStop) {
                        log.warn("[ExecutorRegistryThread] Task executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
            }

            // registry remove
            try {
                RegistryParam registryParam = new RegistryParam(appName, address);
                for (IAppService taskService : AbstractTaskExecutor.getTaskServiceList()) {
                    try {
                        registryParam.setRegisterStatusEnum(RegisterStatusEnum.DISABLED);
                        if (taskService.removeApp(registryParam)) {
                            log.info("[ExecutorRegistryThread] Task registry-remove success, registryParam: {}", registryParam);
                            break;
                        } else {
                            log.info("[ExecutorRegistryThread] Task registry-remove fail, registryParam: {}", registryParam);
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            log.info("[ExecutorRegistryThread] Task registry-remove error, registryParam: {}", registryParam, e);
                        }
                    }

                }
            } catch (Exception e) {
                if (!toStop) {
                    log.error(e.getMessage(), e);
                }
            }

            log.info("[ExecutorRegistryThread] Task executor registry thread destroy");
        });

        registryThread.setDaemon(true);
        registryThread.setName("ExecutorRegistryThread-daemon");
        registryThread.start();
    }

    /**
     * 停止线程
     */
    public void stop() {
        toStop = true;
        // interrupt and wait
        registryThread.interrupt();
        try {
            registryThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
