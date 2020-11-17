package com.ld.peach.job.core.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName AbnormalTaskThread
 * @Description 异常任务处理线程
 * @Author lidong
 * @Date 2020/11/5
 * @Version 1.0
 */
@Slf4j
public class AbnormalTaskThread implements Runnable {

    /**
     * 上锁时长
     */
    private long wait = 0;

    @Override
    public void run() {

        IAdminService appService = PeachJobHelper.getAppService();

        try {

            if (appService.tryLock(TaskConstant.ABNORMAL_TASK_LOCK_KEY)) {
                //处理执行失败的任务
                handleFailTask();

                //处理超时任务
                handleTimeOutTask();
            }
        } catch (Exception ex) {
            if (ex instanceof DuplicateKeyException) {
                // 上锁时长累计
                ++wait;
                log.info("[AbnormalTaskThread] is locking");
            } else {
                log.error("[AbnormalTaskThread] occur error: ", ex);
            }
        } finally {
            //超过90S就释放锁，强制释放
            appService.unlock(TaskConstant.ABNORMAL_TASK_LOCK_KEY, wait > 90);
        }

    }

    /**
     * 处理一段时间内执行失败的任务
     */
    private void handleFailTask() {
        List<TaskInfo> unExecutedTaskList = PeachJobHelper.getAppService().getTaskListByCondition(PeachJobHelper.getJobsProperties().getTaskQueryInterval(),
                Collections.singletonList(TaskExecutionStatus.FAIL));
        log.info("[AbnormalTaskThread] execute find unExecuted task list size: {}", unExecutedTaskList.size());

        if (CollectionUtil.isEmpty(unExecutedTaskList)) {
            return;
        }

        List<TaskInfo> canExecutedTaskList = unExecutedTaskList.stream()
                .filter(taskInfo -> DateUtil.compare(new Date(), taskInfo.getEstimatedExecutionTime()) >= 0).collect(Collectors.toList());

        List<TaskInfo> updateList = unExecutedTaskList.stream()
                .peek(taskInfo -> {
                    taskInfo.setStatus(TaskExecutionStatus.DISTRIBUTED.getCode());
                    taskInfo.setExecutionTimes(Objects.nonNull(taskInfo.getExecutionTimes()) ? taskInfo.getExecutionTimes() + 1 : 1);
                }).collect(Collectors.toList());

        //批量更新发放状态
        int updateNum = PeachJobHelper.getAppService().batchUpdateTaskInfoById(updateList);
        if (updateNum > 0) {
            log.info("[AbnormalTaskThread] success distributed: {} tasks", updateNum);
        }

        //进行任务分发
        PeachJobHelper.getTaskDisruptorTemplate().bulkPublish(canExecutedTaskList);
    }

    /**
     * 处理发送任务执行超时
     */
    private void handleTimeOutTask() {
        List<TaskInfo> noFeedBackTaskList = PeachJobHelper.getAppService().getTaskListByEstimatedTimeCondition(PeachJobHelper.getJobsProperties().getNoFeedBackTaskQueryInterval()
                , Collections.singletonList(TaskExecutionStatus.DISTRIBUTED));

        if (CollectionUtils.isEmpty(noFeedBackTaskList)) {
            return;
        }

        log.info("[handleTimeOutTask] find no feed back task list size: {} timeInterval: {}", noFeedBackTaskList.size(),
                PeachJobHelper.getJobsProperties().getNoFeedBackTaskQueryInterval());

        Date now = new Date();
        //处理已经超时的任务
        noFeedBackTaskList.forEach(tmpTask -> {
            if (tmpTask.getExecutionTimes() < tmpTask.getMaxRetryNum()) {
                tmpTask.setStatus(TaskExecutionStatus.FAIL.getCode());
            } else {
                tmpTask.setStatus(TaskExecutionStatus.ABANDONED.getCode());
            }
        });

        //批量更新任务状态
        PeachJobHelper.getAppService().batchUpdateTaskInfoById(noFeedBackTaskList);
    }
}
