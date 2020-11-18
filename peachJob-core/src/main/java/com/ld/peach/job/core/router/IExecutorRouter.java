package com.ld.peach.job.core.router;

import java.util.List;

/**
 * @InterfaceName IExecutorRouter
 * @Description 执行器路由接口
 * @Author lidong
 * @Date 2020/11/18
 * @Version 1.0
 */
public interface IExecutorRouter {

    /**
     * 路由方法
     *
     * @param addressList 待调用地址列表
     * @return 路由选择执行地址
     */
    String route(List<String> addressList);
}
