package com.ld.peach.job.core.constant.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @EnumName TaskExecutionStatus
 * @Description 任务执行状态
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum TaskExecutionStatus {

    /**
     * 任务执行状态
     */
    NOT_EXECUTION(Short.parseShort("0"), "未执行"),
    SUCCESS(Short.parseShort("1"), "执行成功"),
    FAIL(Short.parseShort("-1"), "执行失败"),
    ABANDONED(Short.parseShort("-2"), "任务废弃");

    private final short code;

    private final String desc;

    /**
     * 根据code 查询任务执行状态
     *
     * @param code code
     * @return 任务执行状态
     */
    public static TaskExecutionStatus getStatusByCode(Short code) {
        if (Objects.isNull(code)) {
            return null;
        }

        return Arrays.stream(TaskExecutionStatus.values())
                .filter(status -> status.getCode() == code).findFirst().orElse(null);
    }
}
