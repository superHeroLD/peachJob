package com.ld.peach.job.core.generic.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName TriggerParam
 * @Description 触发任务参数
 * @Author lidong
 * @Date 2020/9/30
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class DispatchParam implements Serializable {

    private Long taskId;

    private String handler;

    private String param;

    private int timeout;
}
