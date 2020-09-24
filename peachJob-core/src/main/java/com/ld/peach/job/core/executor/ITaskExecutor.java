package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.exception.PeachTaskException;
import com.ld.peach.job.core.generic.ExecutionParams;
import com.ld.peach.job.core.generic.TaskResponse;

/**
 * @InterfaceName ITaskExecutor
 * @Description Task 执行器
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public interface ITaskExecutor {


    /**
     * 执行任务
     *
     * @param params 执行参数
     * @return TaskResponse<T> 执行结果
     * @throws PeachTaskException Task异常
     */
    TaskResponse run(ExecutionParams params) throws PeachTaskException;
}
