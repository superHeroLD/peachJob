package com.ld.peach.job.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ld.peach.job.admin.mapper.TaskInfoMapper;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.util.date.DateTime;
import com.ld.peach.job.core.util.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName TaskService
 * @Description 任务service
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Slf4j
@Service
public class TaskService {

    @Resource
    private TaskInfoMapper taskInfoMapper;

    /**
     * 获取未执行任务列表
     *
     * @param timeInterVal 时间间隔
     * @return 可执行任务列表
     */
    public List<TaskInfo> getUnExecutedTaskList(int timeInterVal) {
        if (timeInterVal <= 0) {
            throw new IllegalArgumentException("timeInterVal is less or equals zero");
        }

        //计算时间间隔
        DateTime startTime = DateUtil.offsetMinute(new Date(), -timeInterVal);
        DateTime endTime = DateUtil.offsetMinute(new Date(), timeInterVal);

        log.info("startTime: {} endTime: {}", startTime, endTime);

        List<TaskInfo> canExecutedTaskList = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .in(TaskInfo::getStatus, TaskExecutionStatus.NOT_EXECUTION, TaskExecutionStatus.FAIL).between(TaskInfo::getEstimatedExecutionTime, startTime, endTime));
        log.info("canExecutedTaskList size: {} between startTime: {} endTime: {}", Objects.isNull(canExecutedTaskList) ? 0 : canExecutedTaskList.size(), startTime, endTime);

        return canExecutedTaskList;
    }

    /**
     * 插入任务信息到 DB
     *
     * @param taskInfo 任务信息
     * @return 插入数量
     */
    public Integer insertTask(TaskInfo taskInfo) {
        return taskInfoMapper.insert(taskInfo);
    }
}
