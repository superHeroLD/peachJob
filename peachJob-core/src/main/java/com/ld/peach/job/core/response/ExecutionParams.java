package com.ld.peach.job.core.response;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName ExecutionParams
 * @Description 执行参数
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Data
@ToString
public class ExecutionParams {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 执行入参
     */
    private String param;
}
