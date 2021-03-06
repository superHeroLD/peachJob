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
     * 插入任务信息到 DB
     *
     * @param taskInfo 任务信息
     * @return 插入数量
     */
    public int insertTask(TaskInfo taskInfo) {
        if (Objects.isNull(taskInfo)) {
            return 0;
        }

        taskInfo.setId(null);
        return taskInfoMapper.insert(taskInfo);
    }

    /**
     * 根据ID获取任务信息
     *
     * @param id 任务ID
     * @return 任务信息
     */
    public TaskInfo getTaskInfoById(Long id) {
        if (Objects.isNull(id) || id <= 0) {
            return null;
        }

        return taskInfoMapper.selectById(id);
    }

    /**
     * 批量插入任务
     *
     * @param taskInfoList 任务集合
     * @return 插入数量
     */
    public int batchInsertTask(List<TaskInfo> taskInfoList) {
        if (CollectionUtil.isEmpty(taskInfoList)) {
            return 0;
        }

        int count = 0;
        for (TaskInfo taskInfo : taskInfoList) {
            count += taskInfoMapper.insert(taskInfo);
        }

        return count;
    }

    /**
     * 获取未执行任务列表
     *
     * @param timeInterVal 时间间隔
     * @return 可执行任务列表
     */
    public List<TaskInfo> getTaskList(int timeInterVal, List<TaskExecutionStatus> statusList) {
        if (timeInterVal <= 0) {
            throw new IllegalArgumentException("timeInterVal is less or equals zero");
        }

        if (Objects.isNull(statusList) || statusList.size() == 0) {
            throw new IllegalArgumentException("statusList empty");
        }

        //计算时间间隔
        DateTime startTime = DateUtil.offsetMinute(new Date(), -timeInterVal);
        DateTime endTime = DateUtil.offsetMinute(new Date(), timeInterVal);

        List<TaskInfo> canExecutedTaskList = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .in(TaskInfo::getStatus, statusList.stream().map(TaskExecutionStatus::getCode).toArray()).between(TaskInfo::getEstimatedExecutionTime, startTime, endTime));
        log.info("[getTaskList] statusList: [{}] list size: {} between startTime: {} endTime: {}", statusList, Objects.isNull(canExecutedTaskList) ? 0 : canExecutedTaskList.size(), startTime, endTime);

        return canExecutedTaskList;
    }

    public List<TaskInfo> getTaskListByEstimatedTimeCondition(int timeInterVal, List<TaskExecutionStatus> statusList) {
        if (timeInterVal <= 0) {
            throw new IllegalArgumentException("timeInterVal is less or equals zero");
        }

        if (Objects.isNull(statusList) || statusList.size() == 0) {
            throw new IllegalArgumentException("statusList empty");
        }

        DateTime startTime = DateUtil.offsetMinute(new Date(), -timeInterVal);
        DateTime endTime = DateUtil.offsetMinute(new Date(), timeInterVal);

        List<TaskInfo> taskInfoList = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .in(TaskInfo::getStatus, statusList.stream().map(TaskExecutionStatus::getCode).toArray()).between(TaskInfo::getEstimatedExecutionTime, startTime, endTime));
        log.info("[getTaskListByEstimatedTimeCondition] statusList: [{}] list size: {} between startTime: {} endTime: {}", statusList, Objects.isNull(taskInfoList) ? 0 : taskInfoList.size(), startTime, endTime);

        return taskInfoList;
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
            try {
                int updateNum = taskInfoMapper.updateById(taskInfo);
                count.addAndGet(updateNum);
            } catch (Exception ex) {
                log.error("batchUpdateTaskInfoById update taskId: {} occur error", taskInfo.getId(), ex);
            }
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
