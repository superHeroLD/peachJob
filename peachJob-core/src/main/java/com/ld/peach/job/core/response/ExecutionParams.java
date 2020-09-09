package com.ld.peach.job.core.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

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
     * 执行参数
     */
    public List<TaskParams> params;

    /**
     * 执行任务参数
     */
    @Data
    @Accessors(chain = true)
    public static class TaskParams {
        /**
         * 任务ID
         */
        private Long taskId;

        /**
         * 执行入参
         */
        private String param;
    }
}
