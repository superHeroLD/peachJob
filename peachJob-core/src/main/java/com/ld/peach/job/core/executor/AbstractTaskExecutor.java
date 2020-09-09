package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.handler.ITaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractTaskExecutor
 * @Description Executor 父类
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public abstract class AbstractTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskExecutor.class);

    private static Map<String, ITaskHandler> taskHandlerStorage = new ConcurrentHashMap<>();

    /**
     * 注册task Handler
     *
     * @param name        handler name
     * @param taskHandler 对应类
     * @return ITaskHandler
     */
    public ITaskHandler registerTaskHandler(String name, ITaskHandler taskHandler) {
        LOGGER.info("[AbstractTaskExecutor] peach-job register task handler name: [{}] handler: [{}]", name, taskHandler);
        return taskHandlerStorage.put(name, taskHandler);
    }

    /**
     * 获取Task handler
     *
     * @param name handler name
     * @return ITaskHandler
     */
    public ITaskHandler getTaskHandler(String name) {
        return taskHandlerStorage.get(name);
    }

    /**
     * 销毁
     */
    public void destory() {
        taskHandlerStorage.clear();
    }

}
