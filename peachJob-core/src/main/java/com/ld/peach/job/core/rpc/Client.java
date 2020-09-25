package com.ld.peach.job.core.rpc;

import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;

/**
 * @ClassName Client
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public abstract class Client {

    protected volatile RpcReferenceBean rpcReferenceBean;

    public void init(RpcReferenceBean rpcReferenceBean) {
        this.rpcReferenceBean = rpcReferenceBean;
    }


    /**
     * async send, bind requestId and future-response
     *
     * @param address    地址
     * @param rpcRequest rpc请求
     * @throws Exception 各种异常
     */
    public abstract void asyncSend(String address, PeachRpcRequest rpcRequest) throws Exception;
}
