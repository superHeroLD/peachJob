package com.ld.peach.job.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @EnumName ResponseCode
 * @Description 返回结果码枚举类
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    /**
     * 请求响应码
     */
    SUCCESS(TaskConstant.CODE_SUCCESS, "200"),
    FAIL(TaskConstant.CODE_FAILED, "500");

    private final int code;

    private final String msg;
}
