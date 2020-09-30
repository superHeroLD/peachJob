package com.ld.peach.job.core.generic.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TriggerParam
 * @Description 触发任务参数
 * @Author lidong
 * @Date 2020/9/30
 * @Version 1.0
 */
@Data
public class TriggerParam implements Serializable {

    private Long taskId;
    /**
     * 租户ID
     */
    private String tenantId;

    private String handler;

    private String param;

    private int timeout;
}
