package com.ld.peach.job.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName LockInfo
 * @Description 锁信息-分布式锁
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class LockInfo implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 持有者
     */
    private String owner;

    /**
     * 创建时间
     */
    private Date createTime;
}
