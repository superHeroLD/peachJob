package com.ld.peach.job.core.handler;

import com.ld.peach.job.core.exception.TaskException;
import com.ld.peach.job.core.response.TaskResponse;

/**
 * @InterfaceName ITaskHandler
 * @Description Task
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public interface ITaskHandler {


    /**
     * 执行任务
     *
     * @param params 入参
     * @return 执行结果
     * @throws TaskException 任务异常
     */
    TaskResponse exectue(String params) throws Exception;
}
