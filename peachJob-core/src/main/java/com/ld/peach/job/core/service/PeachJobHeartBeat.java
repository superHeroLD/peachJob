package com.ld.peach.job.core.service;

import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.starter.JobsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
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

    @Resource
    private JobsProperties jobsProperties;

    @Override
    public void run() {
        log.info("PeachJobHeartBeat begin");

        IAppService appService = PeachJobHelper.getAppService();

        try {
            //获取未执行的任务信息
            List<TaskInfo> unExecutedTaskList = appService.getUnExecutedTaskList(5);

            if (CollectionUtils.isEmpty(unExecutedTaskList)) {

            }


        } catch (Exception ex) {
            log.error("PeachJobHeartBeat occur error: ", ex);
        } finally {

        }

        log.info("PeachJobHeartBeat end");
    }
}
