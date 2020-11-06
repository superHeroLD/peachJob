package com.ld.peach.job.core.service;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

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

    /**
     * 上锁时长
     */
    private long wait = 0;

    @Override
    public void run() {

        IAdminService appService = PeachJobHelper.getAppService();

        try {

            if (appService.tryLock(TaskConstant.ABNORMAL_TASK_LOCK_KEY)) {
                //处理失败任务
                List<TaskInfo> unExecutedTaskList = PeachJobHelper.getAppService().getTaskListByCondition(PeachJobHelper.getJobsProperties().getTaskQueryInterval(),
                        Collections.singletonList(TaskExecutionStatus.FAIL));
                log.info("[AbnormalTaskThread] execute find unExecuted task list size: {}", unExecutedTaskList.size());
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
}
