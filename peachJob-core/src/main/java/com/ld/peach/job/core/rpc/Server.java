package com.ld.peach.job.core.rpc;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @ClassName BaseServer
 * @Description 内置服务器父类
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
@Slf4j
public abstract class Server {

    /**
     * 服务启动回调
     */
    private IRpcCallBack startedCallback;

    /**
     * 服务停止回调
     */
    private IRpcCallBack stoppedCallback;

    public void setStartedCallback(IRpcCallBack startedCallback) {
        this.startedCallback = startedCallback;
    }

    public void setStoppedCallback(IRpcCallBack stoppedCallback) {
        this.stoppedCallback = stoppedCallback;
    }

    /**
     * callback when started
     */
    public void onStarted() {
        if (Objects.nonNull(startedCallback)) {
            try {
                startedCallback.execute();
            } catch (Exception e) {
                log.error("PeachJob RPC server startedCallback occur error", e);
            }
        }
    }

    /**
     * start server
     */
    public abstract void start(final RpcProviderFactory rpcProviderFactory) throws Exception;

    /**
     * stop server
     *
     * @throws Exception 各种异常
     */
    public abstract void stop() throws Exception;

    /**
     * callback when stopped
     */
    public void onStopped() {
        if (Objects.nonNull(stoppedCallback)) {
            try {
                stoppedCallback.execute();
            } catch (Exception e) {
                log.error("PeachJob RPC server stoppedCallback occur error", e);
            }
        }
    }
}
