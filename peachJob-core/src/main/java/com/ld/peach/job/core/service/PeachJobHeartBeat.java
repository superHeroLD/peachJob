package com.ld.peach.job.core.service;

import cn.hutool.core.date.DateUtil;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName PeachJobHeartBeat
 * @Description 心跳类 承载了很多逻辑
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Slf4j
public class PeachJobHeartBeat implements Runnable {

    @Override
    public void run() {
        log.info("PeachJobHeartBeat begin");

        IAppService appService = PeachJobHelper.getAppService();

        try {
            //获取未执行的任务信息
            List<TaskInfo> unExecutedTaskList = appService.getUnExecutedTaskList(PeachJobHelper.getJobsProperties().getTaskQueryInterval());

            if (CollectionUtils.isEmpty(unExecutedTaskList)) {
                log.info("[PeachJobHeartBeat] There is no task can be performed");
                return;
            }

            //过滤一下可以执行的任务 TODO 这里要不要写一个时间轮来控制任务的精确实行时间，精确到秒或者毫秒级别
            Date now = new Date();
            List<TaskInfo> canExecutedTaskList = unExecutedTaskList.stream()
                    .filter(taskInfo -> DateUtil.compare(now, taskInfo.getEstimatedExecutionTime()) >= 0).collect(Collectors.toList());

            //进行任务分发
            PeachJobHelper.getTaskDisruptorTemplate().bulkPublish(canExecutedTaskList);

            List<TaskInfo> updateList = canExecutedTaskList.stream()
                    .peek(taskInfo -> taskInfo.setStatus(TaskExecutionStatus.DISTRIBUTED.getCode()))
                    .collect(Collectors.toList());

            //批量更新发放状态
            int updateNum = PeachJobHelper.getAppService().batchUpdateTaskInfoById(updateList);
            if (updateNum > 0) {
                log.info("[PeachJobHeartBeat] success distributed: {} tasks", updateNum);
            }

            //TODO 这里应该分发超过执行时间的任务，失败过的任务，还是创建一个新的心跳任务？
        } catch (Exception ex) {
            log.error("PeachJobHeartBeat occur error: ", ex);
        } finally {

        }
    }
}
