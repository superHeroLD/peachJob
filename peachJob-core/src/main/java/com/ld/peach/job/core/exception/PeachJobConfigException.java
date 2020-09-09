package com.ld.peach.job.core.exception;

/**
 * @ClassName PeachJobInitException
 * @Description Peach Job 初始化 Exception
 * @Author lidong
 * @Date 2020/9/9
 * @Version 1.0
 */
public class PeachJobConfigException extends RuntimeException{
    public PeachJobConfigException() {
    }

    public PeachJobConfigException(String message) {
        super(message);
    }

    public PeachJobConfigException(Throwable cause) {
        super(cause);
    }

    public PeachJobConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
