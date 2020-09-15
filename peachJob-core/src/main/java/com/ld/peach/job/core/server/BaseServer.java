package com.ld.peach.job.core.server;

import com.ld.peach.job.core.rpc.RpcProviderFactory;

/**
 * @ClassName BaseServer
 * @Description 内置服务器父类
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public abstract class BaseServer {

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
}
