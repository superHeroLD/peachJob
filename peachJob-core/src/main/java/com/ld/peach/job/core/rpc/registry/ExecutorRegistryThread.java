package com.ld.peach.job.core.rpc.registry;

import com.ld.peach.job.core.constant.RegisterStatusEnum;
import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.executor.AbstractTaskExecutor;
import com.ld.peach.job.core.model.params.RegistryParam;
import com.ld.peach.job.core.service.ITaskService;
import com.ld.peach.job.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName ExecuteregistyThread
 * @Description 执行服务注册线程
 * @Author lidong
 * @Date 2020/10/12
 * @Version 1.0
 */
public class ExecutorRegistryThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private Thread registryThread;

    private volatile boolean toStop = false;

    /**
     * 单例
     */
    private static ExecutorRegistryThread INSTANCE = new ExecutorRegistryThread();

    public static ExecutorRegistryThread getInstance() {
        return INSTANCE;
    }

    /**
     * 启动服务注册线程
     *
     * @param appName
     * @param address
     */
    public void start(final String appName, final String address) {
        if (StringUtil.isBlank(appName)) {
            LOGGER.warn("[ExecutorRegistryThread] app is null");
            return;
        }

        if (StringUtil.isBlank(address)) {
            LOGGER.warn("[ExecutorRegistryThread] address is null");
            return;
        }

        registryThread = new Thread(() -> {
            //不断的轮训注册
            while (!toStop) {
                try {
                    RegistryParam registryParam = new RegistryParam(appName, address);
                    for (ITaskService taskService : AbstractTaskExecutor.getTaskServiceList()) {
                        try {
                            if (taskService.registry(registryParam)) {
                                LOGGER.info("[ExecutorRegistryThread] Task registry success, registryParam: {}", registryParam);
                                break;
                            } else {
                                LOGGER.info("[ExecutorRegistryThread] Task registry fail, registryParam: {}", registryParam);
                            }
                        } catch (Exception e) {
                            LOGGER.info("[ExecutorRegistryThread] Task registry error, registryParam: {}", registryParam, e);
                        }

                    }
                } catch (Exception e) {
                    if (!toStop) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                try {
                    if (!toStop) {
                        TimeUnit.SECONDS.sleep(TaskConstant.BEAT_TIMEOUT);
                    }
                } catch (InterruptedException e) {
                    if (!toStop) {
                        LOGGER.warn("[ExecutorRegistryThread] Task executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
            }

            // registry remove
            try {
                RegistryParam registryParam = new RegistryParam(appName, address);
                for (ITaskService taskService : AbstractTaskExecutor.getTaskServiceList()) {
                    try {
                        registryParam.setRegisterStatusEnum(RegisterStatusEnum.DISABLED);
                        if (taskService.removeApp(registryParam)) {
                            LOGGER.info("[ExecutorRegistryThread] Task registry-remove success, registryParam: {}", registryParam);
                            break;
                        } else {
                            LOGGER.info("[ExecutorRegistryThread] Task registry-remove fail, registryParam: {}", registryParam);
                        }
                    } catch (Exception e) {
                        if (!toStop) {
                            LOGGER.info("[ExecutorRegistryThread] Task registry-remove error, registryParam: {}", registryParam, e);
                        }
                    }

                }
            } catch (Exception e) {
                if (!toStop) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            LOGGER.info("[ExecutorRegistryThread] Task executor registry thread destroy");
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
            LOGGER.error(e.getMessage(), e);
        }
    }
}
