package com.ld.peach.job.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName TaskLog
 * @Description 任务执行日志
 * @Author lidong
 * @Date 2020/11/2
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class TaskLog {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务执行状态
     * 是否成功
     */
    private Short status;

    /**
     * 执行地址
     */
    private String address;

    /**
     * 执行结果
     * 也包括错误信息
     */
    private String result;

    /**
     * 创建时间
     */
    private Date createTime;
}
