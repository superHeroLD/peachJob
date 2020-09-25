package com.ld.peach.job.core.rpc.invoker.reference;

import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.invoker.call.PeachRpcInvokeCallback;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName RpcReferenceBean
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public class RpcReferenceBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceBean.class);

    private IPeachJobRpcSerializer serializer;

    private Class<?> iface;
    private String version;

    private long timeout = 1000;

    private String address;
    private String accessToken;

    private PeachRpcInvokeCallback invokeCallback;

    private PeachRpcInvokerFactory invokerFactory;

    public PeachRpcInvokerFactory getInvokerFactory() {
        return invokerFactory;
    }

    public IPeachJobRpcSerializer getSerializer() {
        return serializer;
    }

    public long getTimeout() {
        return timeout;
    }


}
