package com.ld.peach.job.core.disruptor;

import com.ld.peach.job.core.model.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaskInfoEvent
 * @Description 任务事件
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {

    private TaskInfo taskInfo;
}
