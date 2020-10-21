package com.ld.peach.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ServiceRegisty
 * @Description 服务注册
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegistry implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 服务名
     */
    private String appName;

    /**
     * 地址 = IP:PORT 例如：127.0.0.1:9999
     */
    private String address;

    /**
     * 状态：0、启用 1、已禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
