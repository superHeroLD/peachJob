package com.ld.peach.job.core.rpc;

import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;

/**
 * @ClassName RpcProviderFactory
 * @Description RPC provider 实现工厂
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public class RpcProviderFactory {

    private String ip;
    private int port;

    /**
     * token
     */
    private String accessToken;

    private IPeachJobRpcSerializer serializer;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public IPeachJobRpcSerializer getSerializer() {
        return serializer;
    }
}
