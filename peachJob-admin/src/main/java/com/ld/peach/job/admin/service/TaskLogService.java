package com.ld.peach.job.admin.service;

import com.alibaba.fastjson.JSON;
import com.ld.peach.job.admin.mapper.TaskLogMapper;
import com.ld.peach.job.core.async.TaskLogThreadPool;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.TaskLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName TaskLogService
 * @Description 任务日志服务
 * @Author lidong
 * @Date 2020/11/2
 * @Version 1.0
 */
@Slf4j
@Service
public class TaskLogService {

    @Resource
    private TaskLogMapper taskLogMapper;

    /**
     * 记录任务日志
     *
     * @param taskInfo 任务信息
     * @param address  地址
     * @param response 返回请求
     */
    public void recordTaskLog(TaskInfo taskInfo, String address, TaskResponse response) {
        if (Objects.isNull(taskInfo) || Objects.isNull(response)) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                TaskLog taskLog = new TaskLog()
                        .setTaskId(taskInfo.getId())
                        .setAddress(address)
                        .setCreateTime(new Date())
                        .setResult(Objects.nonNull(response.getData()) ? JSON.toJSONString(response.getData()) : "")
                        .setStatus((short) response.getCode());

                taskLogMapper.insert(taskLog);
            } catch (Exception ex) {
                log.error("recordTaskLog task log occur error: ", ex);
            }

        }, TaskLogThreadPool.getInstance());
    }
}
