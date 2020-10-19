package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.exception.PeachTaskException;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.generic.param.TriggerParam;
import com.ld.peach.job.core.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @ClassName TaskExecutor
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/30
 * @Version 1.0
 */
@Slf4j
public class TaskExecutor implements ITaskExecutor {

    /**
     * 执行任务调度
     *
     * @param triggerParam 触发参数
     * @return 任务处理结果
     */
    @Override
    public TaskResponse run(TriggerParam triggerParam) throws PeachTaskException {
        if (Objects.isNull(triggerParam)) {
            throw new PeachTaskException("TaskExecutor execute triggerParam is null");
        }
        ITaskHandler taskHandler = AbstractTaskExecutor.getTaskHandler(triggerParam.getHandler());
        if (Objects.isNull(taskHandler)) {
            throw new PeachTaskException("TaskExecutor not found execute handler:" + triggerParam.getHandler());
        }

        return taskHandler.execute(triggerParam.getTenantId(), triggerParam.getParam());
    }
}
