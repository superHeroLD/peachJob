package com.ld.peach.job.core.generic;

import com.ld.peach.job.core.constant.ResponseCode;
import com.ld.peach.job.core.constant.TaskConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @ClassName TaskResponse
 * @Description 任务执行返回结果
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Data
public class TaskResponse<T> implements Serializable {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应信息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    public TaskResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> TaskResponse<T> success(T data) {
        return new TaskResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), data);
    }

    public static <T> TaskResponse<T> success() {
        return new TaskResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    public static <T> TaskResponse<T> fail() {
        return new TaskResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }

    public static <T> TaskResponse<T> fail(String msg) {
        return new TaskResponse<>(ResponseCode.FAIL.getCode(), msg, null);
    }

    public boolean isSuccess() {
        return Objects.equals(TaskConstant.CODE_SUCCESS, this.getCode());
    }
}
