package com.ld.peach.job.core.disruptor;

import com.ld.peach.job.core.model.TaskInfo;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName TaskDisruptorTemplate
 * @Description Task Disruptor 相当于高性能内存队列
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
@Slf4j
public class TaskDisruptorTemplate {

    @Resource
    protected Disruptor<TaskEvent> disruptor;

    /**
     * 发布事件
     *
     * @param taskInfo 任务信息
     */
    public void publish(TaskInfo taskInfo) {
        if (Objects.isNull(taskInfo)) {
            return;
        }

        log.info("[TaskDisruptorTemplate] send taskInfo: {}", taskInfo);
        disruptor.publishEvent((event, sequence, bb) -> event.setTaskInfo(bb), taskInfo);
    }

    /**
     * 批量发送
     *
     * @param taskInfoList 任务信息列表
     */
    public void bulkPublish(List<TaskInfo> taskInfoList) {
        if (CollectionUtils.isEmpty(taskInfoList)) {
            return;
        }

        taskInfoList.forEach(this::publish);
    }
}
