package com.ld.peach.job.core.handler;

import com.ld.peach.job.core.exception.PeachTaskException;
import com.ld.peach.job.core.generic.TaskResponse;

import java.lang.reflect.Method;

/**
 * @ClassName TaskHandler
 * @Description Task Handler
 * @Author lidong
 * @Date 2020/9/9
 * @Version 1.0
 */
public class TaskHandler implements ITaskHandler {

    /**
     * Spring Bean
     */
    private final Object target;

    /**
     * 被 @PeachTask 注解的方法
     */
    private final Method method;

    public TaskHandler(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    /**
     * 执行方法
     *
     * @param params 入参
     */
    @Override
    public TaskResponse execute(String params) throws PeachTaskException {
        try {
            method.invoke(target, params);
            return TaskResponse.success();
        } catch (Exception ex) {
            throw new PeachTaskException(ex);
        }
    }
}
