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
@ToString
@AllArgsConstructor
public enum ResponseCode {

    /**
     * 请求响应码
     */
    SUCCESS(JobConstant.CODE_SUCCESS, "成功"),
    FAIL(JobConstant.CODE_FAILED, "失败");

    private final int code;

    private final String msg;
}
