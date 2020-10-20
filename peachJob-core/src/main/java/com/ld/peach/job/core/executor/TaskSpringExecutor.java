package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.handler.ITaskHandler;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @ClassName TaskSpringExecutor
 * @Description Spring Executor
 * 内嵌到Spring中执行
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Slf4j
public class TaskSpringExecutor extends AbstractTaskExecutor implements ApplicationContextAware {

    @Resource
    private IPeachJobRpcSerializer rpcSerializer;

    /**
     * Spring上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TaskSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public IPeachJobRpcSerializer getRpcSerializer() {
        return rpcSerializer;
    }

    @Override
    public void start() throws Exception {
        initTaskHandlerRepository(applicationContext);

        super.start();
    }

    /**
     * 从spring中获取 task handler
     *
     * @param applicationContext spring上下文
     */
    private void initTaskHandlerRepository(ApplicationContext applicationContext) {
        if (Objects.isNull(applicationContext)) {
            log.info("[TaskSpringExecutor] applicationContext is null");
            return;
        }

        //TODO 这里在思考下，是用接口还是注解？
        String[] taskHandlerArr = applicationContext.getBeanNamesForType(ITaskHandler.class);
        if (taskHandlerArr.length > 0) {
            for (String taskHandler : taskHandlerArr) {
                putTaskHandler(taskHandler, (ITaskHandler) applicationContext.getBean(taskHandler));
            }
        }
    }
}
