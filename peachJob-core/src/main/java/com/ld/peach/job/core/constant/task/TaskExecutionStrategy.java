package com.ld.peach.job.core.constant.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @EnumName TaskExecutionStrategy
 * @Description 任务执行策略
 * @Author lidong
 * @Date 2020/10/25
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum TaskExecutionStrategy {

    /**
     * 任务执行状态
     */
    DEFAULT(Short.parseShort("0"), "默认执行策略");

    private final short code;

    private final String desc;
}
