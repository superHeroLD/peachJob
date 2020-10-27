package com.ld.peach.job.core.model;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @ClassName TaskInfo
 * @Description 任务信息
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Data
public class TaskInfo implements Serializable {
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
    private Boolean valid;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", TaskInfo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("createTime=" + DateUtil.formatDate(createTime))
                .add("updateTime=" + DateUtil.formatDate(updateTime))
                .add("valid=" + valid)
                .add("estimatedExecutionTime=" + estimatedExecutionTime)
                .add("actualExecutionTime=" + actualExecutionTime)
                .add("taskHandler='" + taskHandler + "'")
                .add("taskName='" + taskName + "'")
                .add("executeParams='" + executeParams + "'")
                .add("maxRetryNum=" + maxRetryNum)
                .add("executionTimes=" + executionTimes)
                .add("status=" + status)
                .add("executionStrategy=" + executionStrategy)
                .add("result='" + result + "'")
                .toString();
    }
}
