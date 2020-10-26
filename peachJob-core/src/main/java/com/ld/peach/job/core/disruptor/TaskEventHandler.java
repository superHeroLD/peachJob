package com.ld.peach.job.core.disruptor;

import cn.hutool.core.date.DateUtil;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.dispatch.TaskDispatchCenter;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.service.PeachJobHelper;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

/**
 * @ClassName TaskEventHandler
 * @Description Task 事件Handler
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
@Slf4j
public class TaskEventHandler implements EventHandler<TaskEvent> {

    @Override
    public void onEvent(TaskEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (Objects.isNull(event) || Objects.isNull(event.getTaskInfo())) {
            log.error("TaskEventHandler receive null task event");
        }

        TaskInfo taskInfo = event.getTaskInfo();

        //校验是否可以执行 1.是否到达了最大执行次数. 2.是否到了执行时间
        if (Objects.nonNull(taskInfo.getMaxRetryNum()) && taskInfo.getMaxRetryNum() > 0) {
            if (taskInfo.getExecutionTimes() >= taskInfo.getMaxRetryNum()) {
                //更新任务执行状态为废弃状态
                if (!Objects.equals(TaskExecutionStatus.getStatusByCode(taskInfo.getStatus()), TaskExecutionStatus.ABANDONED)) {
                    short oldStatus = taskInfo.getStatus();

                    taskInfo.setStatus(TaskExecutionStatus.ABANDONED.getCode());
                    PeachJobHelper.getAppService().updateTaskInfoById(taskInfo);
                    log.info("[TaskEventHandler] update taskId: {} from old status: {} to status: {} ", taskInfo.getId(), oldStatus, TaskExecutionStatus.ABANDONED);
                    return;
                }
            }
        }

        if (DateUtil.compare(new Date(), taskInfo.getEstimatedExecutionTime()) < 0) {
            return;
        }

        boolean result = TaskDispatchCenter.processTask(taskInfo);
        log.info("Task execution: {} result: {}", taskInfo, result);

        if (Boolean.FALSE == result) {
            taskInfo.setStatus(TaskExecutionStatus.FAIL.getCode());
        } else {
            taskInfo.setStatus(TaskExecutionStatus.SUCCESS.getCode());
        }

        taskInfo.setExecutionTimes(taskInfo.getExecutionTimes() + 1);
        PeachJobHelper.getAppService().updateTaskInfoById(taskInfo);
    }
}
