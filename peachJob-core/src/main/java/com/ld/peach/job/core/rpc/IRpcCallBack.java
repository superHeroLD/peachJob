package com.ld.peach.job.core.rpc;

import com.ld.peach.job.core.exception.PeachRpcException;

/**
 * @InterfaceName IRpcCallBack
 * @Description RPC 回调接口
 * @Author lidong
 * @Date 2020/9/16
 * @Version 1.0
 */
public interface IRpcCallBack {

    /**
     * 执行回调逻辑
     */
    void execute() throws PeachRpcException;
}
