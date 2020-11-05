package com.ld.peach.job.core.service;

import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName AbnormalTaskThread
 * @Description 异常任务处理线程
 * @Author lidong
 * @Date 2020/11/5
 * @Version 1.0
 */
@Slf4j
public class AbnormalTaskThread implements Runnable {


    @Override
    public void run() {

        //处理失败任务
        List<TaskInfo> unExecutedTaskList = PeachJobHelper.getAppService().getTaskListByCondition(PeachJobHelper.getJobsProperties().getTaskQueryInterval(),
                Collections.singletonList(TaskExecutionStatus.FAIL));
        log.info("[AbnormalTaskThread] execute find unExecuted task list size: {}", unExecutedTaskList.size());
    }
}
