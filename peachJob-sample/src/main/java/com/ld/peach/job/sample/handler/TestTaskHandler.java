package com.ld.peach.job.sample.handler;

import com.ld.peach.job.core.exception.PeachTaskException;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.handler.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestTaskHandler
 * @Description 测试任务Handler 放到spring 中
 * @Author lidong
 * @Date 2020/10/27
 * @Version 1.0
 */
@Slf4j
@Component("TestTaskHandler")
public class TestTaskHandler implements ITaskHandler {

    @Override
    public TaskResponse execute(String params) throws PeachTaskException {
        log.info("[TestTaskHandler] receive params: {}", params);

        return TaskResponse.success("TestTask success");
    }
}
