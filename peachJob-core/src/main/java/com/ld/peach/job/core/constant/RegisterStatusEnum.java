package com.ld.peach.job.core.constant;

/**
 * @EnumName RegisterStatusEnum
 * @Description 服务注册状态
 * @Author lidong
 * @Date 2020/9/28
 * @Version 1.0
 */
public enum RegisterStatusEnum {

    /**
     * 启用
     */
    ENABLED(0),
    /**
     * 已禁用
     */
    DISABLED(1);

    private final Integer value;

    RegisterStatusEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
