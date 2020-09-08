package com.ld.peach.job.core.exception;

/**
 * @ClassName TaskException
 * @Description Task异常
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public class TaskException extends RuntimeException {

    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
