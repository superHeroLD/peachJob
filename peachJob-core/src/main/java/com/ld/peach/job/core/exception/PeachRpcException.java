package com.ld.peach.job.core.exception;

/**
 * @ClassName PeachRpcException
 * @Description RPC Exception
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public class PeachRpcException extends RuntimeException {

    public PeachRpcException() {
    }

    public PeachRpcException(String message) {
        super(message);
    }

    public PeachRpcException(Throwable cause) {
        super(cause);
    }

    public PeachRpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
