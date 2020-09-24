package com.ld.peach.job.core.rpc.invoker.call;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcFutureResponse;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.util.StringUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName PeachRpcInvokeFuture
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public class PeachRpcInvokeFuture<T> implements Future<T> {

    private final PeachRpcFutureResponse futureResponse;

    public PeachRpcInvokeFuture(PeachRpcFutureResponse futureResponse) {
        this.futureResponse = futureResponse;
    }

    public void stop() {

    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return futureResponse.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return futureResponse.isCancelled();
    }

    @Override
    public boolean isDone() {
        return futureResponse.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new PeachRpcException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            PeachRpcResponse response = futureResponse.get(timeout, unit);
            if (StringUtil.isNotBlank(response.getErrorMsg())) {
                throw new PeachRpcException(response.getErrorMsg());
            }

            return (T) response.getResult();
        } finally {
            stop();
        }
    }

    @SuppressWarnings("rawtypes")
    private static ThreadLocal<PeachRpcInvokeFuture> threadInvokerFuture = new ThreadLocal<>();

    /**
     * get future
     */
    @SuppressWarnings("unchecked")
    public static <T> Future<T> getFuture(Class<T> type) {
        Future<T> future = (Future<T>) threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return future;
    }

    /**
     * set future
     */
    @SuppressWarnings("rawtypes")
    public static void setFuture(PeachRpcInvokeFuture future) {
        threadInvokerFuture.set(future);
    }

    /**
     * remove future
     */
    public static void removeFuture() {
        threadInvokerFuture.remove();
    }
}
