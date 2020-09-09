package com.ld.peach.job.core.model;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName TaskInfo
 * @Description 任务信息
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Data
public class TaskInfo {
    /**
     * 任务主键ID
     */
    private Long id;

    /**
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 任务最后一次更新时间
     */
    private Date updateTime;

    /**
     * 任务是否有效
     */
    private Long valid;

    /**
     * 任务预期执行时间
     */
    private Date estimatedExecutionTime;

    /**
     * 任务实际执行时间
     */
    private Date actualExecutionTime;

    /**
     * 任务执行器名称
     */
    private String taskHandler;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 执行参数
     */
    private String executeParams;

    /**
     * 最大重试次数
     */
    private Integer maxRetryNum;

    /**
     * 执行次数
     */
    private Integer executionTimes;

    /**
     * 执行状态
     */
    private Short status;

    /**
     * 执行策略
     */
    private Short executionStrategy;

    /**
     * 执行结果
     */
    private String result;
}
