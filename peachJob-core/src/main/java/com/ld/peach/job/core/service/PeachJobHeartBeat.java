package com.ld.peach.job.core.service;

import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

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

            //进行任务分发
            PeachJobHelper.getTaskDisruptorTemplate().bulkPublish(unExecutedTaskList);

        } catch (Exception ex) {
            log.error("PeachJobHeartBeat occur error: ", ex);
        } finally {

        }

        log.info("PeachJobHeartBeat end");
    }
}
