package com.ld.peach.job.admin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ld.peach.job.admin.mapper.TaskInfoMapper;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.model.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
                .in(TaskInfo::getStatus, Arrays.asList(TaskExecutionStatus.NOT_EXECUTION.getCode(), TaskExecutionStatus.FAIL.getCode())).between(TaskInfo::getEstimatedExecutionTime, startTime, endTime));
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

    /**
     * 批量更新任务信息
     * 必须有ID
     *
     * @param taskInfoList 任务集合
     * @return 更新数量
     */
    public int batchUpdateTaskInfoById(List<TaskInfo> taskInfoList) {
        if (CollectionUtil.isEmpty(taskInfoList)) {
            return 0;
        }

        List<TaskInfo> canUpdateList = taskInfoList.stream()
                .filter(tmpTask -> Objects.nonNull(tmpTask.getId())).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(canUpdateList)) {
            log.info("All tasks have no id");
            return 0;
        }

        AtomicInteger count = new AtomicInteger(0);
        canUpdateList.forEach(taskInfo -> {
            int updateNum = taskInfoMapper.updateById(taskInfo);
            count.addAndGet(updateNum);
        });

        return count.get();
    }

    /**
     * 根据Id更新任务信息
     *
     * @param taskInfo 任务信息
     * @return 是否更新成功
     */
    public boolean updateTaskInfoById(TaskInfo taskInfo) {
        if (Objects.isNull(taskInfo)) {
            return false;
        }

        if (Objects.isNull(taskInfo.getId())) {
            throw new IllegalArgumentException("task id can't be null");
        }

        return Objects.equals(taskInfoMapper.updateById(taskInfo), 1);
    }
}
