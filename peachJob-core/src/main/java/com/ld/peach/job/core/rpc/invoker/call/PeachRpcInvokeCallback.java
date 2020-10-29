package com.ld.peach.job.core.rpc.invoker.call;

/**
 * @ClassName PeachRpcInvokeCallback
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public abstract class PeachRpcInvokeCallback<T> {

    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable exception);

    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<PeachRpcInvokeCallback> threadInvokerFuture = new ThreadLocal<>();

    @SuppressWarnings("rawtypes")
    public static PeachRpcInvokeCallback getCallback() {
        PeachRpcInvokeCallback invokeCallback = threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return invokeCallback;
    }

    @SuppressWarnings("rawtypes")
    public static void setCallback(PeachRpcInvokeCallback invokeCallback) {
        threadInvokerFuture.set(invokeCallback);
    }

    public static void removeCallback() {
        threadInvokerFuture.remove();
    }
}
