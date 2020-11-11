package com.ld.peach.job.test.service;

import com.ld.peach.job.admin.service.TaskService;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.builder.PeachTaskBuilder;
import com.ld.peach.job.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TaskTest
 * @Description 任务Servce 单元测试
 * @Author lidong
 * @Date 2020/10/25
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
public class TaskServiceTest extends BaseTest {

    @Resource
    private TaskService taskService;

    @Test
    public void insertTaskTest() {
        TaskInfo taskInfo = PeachTaskBuilder.newBuilder()
                .taskHandler("TestTaskHandler")
                .taskName("TestTask")
                .executeParams("This is a test task")
                .executionDate(1, TimeUnit.MINUTES)
                .taskStatus(TaskExecutionStatus.DISTRIBUTED.getCode())
                .build();

        Integer insertNum = taskService.insertTask(taskInfo);
        Assert.assertEquals("insertNum not equals 1", 1, (int) insertNum);
    }

    @Test
    public void getUnExecutedTaskListTest() {
        List<TaskInfo> unExecutedTaskList = taskService.getTaskList(10, Collections.singletonList(TaskExecutionStatus.NOT_EXECUTION));
        log.info("unExecutedTaskList size: {}", unExecutedTaskList.size());
    }
}
