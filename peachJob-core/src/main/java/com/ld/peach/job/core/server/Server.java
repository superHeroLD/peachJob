package com.ld.peach.job.core.server;

import com.ld.peach.job.core.rpc.IRpcCallBack;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @ClassName BaseServer
 * @Description 内置服务器父类
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public abstract class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * 服务启动回调
     */
    private IRpcCallBack startedCallback;

    /**
     * 服务停止回调
     */
    private IRpcCallBack stoppedCallback;

    /**
     * callback when started
     */
    public void onStarted() {
        if (Objects.nonNull(startedCallback)) {
            try {
                startedCallback.execute();
            } catch (Exception e) {
                LOGGER.error("PeachJob RPC server startedCallback occur error", e);
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
                LOGGER.error("PeachJob RPC server stoppedCallback occur error", e);
            }
        }
    }
}
