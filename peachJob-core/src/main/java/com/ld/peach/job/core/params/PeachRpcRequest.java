package com.ld.peach.job.core.params;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PeachRpcRequest
 * @Description RPC request
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
@Data
public class PeachRpcRequest implements Serializable {
    private String requestId;
    private long createMillisTime;
    private String accessToken;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private String version;
}
