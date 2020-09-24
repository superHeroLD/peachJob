package com.ld.peach.job.core.generic;

import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.invoker.call.PeachRpcInvokeCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName PeachRpcFutureResponse
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public class PeachRpcFutureResponse implements Future<PeachRpcResponse> {

    private PeachRpcInvokerFactory peachRpcInvokerFactory;

    private PeachRpcRequest request;
    private PeachRpcResponse response;

    /**
     * future lock
     */
    private boolean done = false;
    private final Object lock = new Object();

    private PeachRpcInvokeCallback invokeCallback;


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public PeachRpcResponse get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public PeachRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    public void setResponse(PeachRpcResponse response) {
        this.response = response;
        synchronized (lock) {
            done = true;
            lock.notifyAll();
        }
    }

    public PeachRpcInvokeCallback getInvokeCallback() {
        return invokeCallback;
    }
}
