package com.ld.peach.job.core.generic;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PeachResponse
 * @Description RPC response
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
@Data
public class PeachRpcResponse implements Serializable {
    private String requestId;
    private Integer code;
    private String errorMsg;
    private Object result;
}
