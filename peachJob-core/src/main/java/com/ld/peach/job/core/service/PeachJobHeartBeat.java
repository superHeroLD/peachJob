package com.ld.peach.job.core.service;

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

    /**
     * 上锁时长
     */
    private long wait = 0;

    /**
     * 心跳时长
     */
    private long beat = 0;

    @Override
    public void run() {
        log.info("PeachJobHeartBeat begin");

        IAdminService appService = PeachJobHelper.getAppService();

        try {
            if (appService.tryLock(TaskConstant.DEFAULT_LOCK_KEY)) {
                //重置计时器
                wait = 0;

                //获取未执行的任务信息
                List<TaskInfo> unExecutedTaskList = appService.getTaskListByCondition(PeachJobHelper.getJobsProperties().getTaskQueryInterval(),
                        Collections.singletonList(TaskExecutionStatus.NOT_EXECUTION));

                if (CollectionUtils.isEmpty(unExecutedTaskList)) {
                    return;
                }

                //过滤一下可以执行的任务 TODO 这里要不要写一个时间轮来控制任务的精确实行时间，精确到秒或者毫秒级别
                Date now = new Date();
                List<TaskInfo> canExecutedTaskList = unExecutedTaskList.stream()
                        .filter(taskInfo -> DateUtil.compare(now, taskInfo.getEstimatedExecutionTime()) >= 0).collect(Collectors.toList());

                List<TaskInfo> updateList = canExecutedTaskList.stream()
                        .peek(taskInfo -> taskInfo.setStatus(TaskExecutionStatus.DISTRIBUTED.getCode()))
                        .collect(Collectors.toList());

                //批量更新发放状态
                int updateNum = PeachJobHelper.getAppService().batchUpdateTaskInfoById(updateList);
                if (updateNum > 0) {
                    log.info("[PeachJobHeartBeat] success distributed: {} tasks", updateNum);
                }

                //进行任务分发
                PeachJobHelper.getTaskDisruptorTemplate().bulkPublish(canExecutedTaskList);

                //TODO 这里应该分发超过执行时间的任务，失败过的任务，还是创建一个新的心跳任务？
            }
        } catch (Exception ex) {
            if (ex instanceof DuplicateKeyException) {
                // 上锁时长累计
                ++wait;
                log.info("[PeachJobHeartBeat] is locking");
            } else {
                log.error("[PeachJobHeartBeat] occur error: ", ex);
            }
        } finally {
            //超过90S就释放锁，强制释放
            appService.unlock(TaskConstant.DEFAULT_LOCK_KEY, wait > 90);

            // 清理异常注册节点
            ++beat;
            if (beat > TaskConstant.BEAT_TIMEOUT) {
                try {
                    appService.cleanTimeoutApp();
                    beat = 0;
                } catch (Exception ex) {
                    log.error("[PeachJobHeartBeat] clean time out app occur error: ", ex);
                }
            }
        }
    }
}
