package com.ld.peach.job.core.rpc.registry;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @InterfaceName IServiceRegistry
 * @Description 服务注册接口
 * @Author lidong
 * @Date 2020/9/16
 * @Version 1.0
 */
public interface IServiceRegistry {

    /**
     * 启动
     *
     * @param param 参数
     */
    void start(Map<String, String> param);

    /**
     * 停止
     */
    void stop();

    /**
     * registry service, for mult
     *
     * @param keys  service key
     * @param value service value/ip:port
     * @return
     */
    boolean registry(Set<String> keys, String value);


    /**
     * remove service, for mult
     *
     * @param keys
     * @param value
     * @return
     */
    boolean remove(Set<String> keys, String value);

    /**
     * discovery services, for mult
     *
     * @param keys
     * @return
     */
    Map<String, TreeSet<String>> discovery(Set<String> keys);

    /**
     * discovery service, for one
     *
     * @param key service key
     * @return service value/ip:port
     */
    TreeSet<String> discovery(String key);

}
