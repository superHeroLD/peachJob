package com.ld.peach.job.core.exception;

/**
 * @ClassName TaskException
 * @Description Task异常
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public class PeachTaskException extends Exception {

    public PeachTaskException() {
    }

    public PeachTaskException(String message) {
        super(message);
    }

    public PeachTaskException(Throwable cause) {
        super(cause);
    }

    public PeachTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
