package com.ld.peach.job.core.rpc.invoker.common;

/**
 * @InterfaceName PeachRpcGenericService
 * @Description Rpc 通用调用接口
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public interface PeachRpcGenericService {

    /**
     * 调用方法
     *
     * @param iface          接口名称
     * @param version        接口版本
     * @param method         方法名称
     * @param parameterTypes 参数类型, 例如 "int、java.lang.Integer、java.util.List、java.util.Map ..."
     * @param args           参数
     * @return 调用结果
     */
    Object invoke(String iface, String version, String method, String[] parameterTypes, Object[] args);
}
