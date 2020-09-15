package com.ld.peach.job.core.rpc.serialize;

/**
 * @InterfaceName IPeachJobRpcSerializer
 * @Description RPC 序列化接口
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
public interface IPeachJobRpcSerializer {

    /**
     * 序列化对象
     *
     * @param obj 对象
     * @param <T> 返回序列化字节数组
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化字节数组为类对象
     *
     * @param bytes 字节数组
     * @param clazz 待反序列化类
     * @param <T>   反序列化对象
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
