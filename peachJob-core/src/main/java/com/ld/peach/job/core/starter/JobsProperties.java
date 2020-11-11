package com.ld.peach.job.core.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 启动参数
 */
@Data
@ConfigurationProperties(JobsProperties.PREFIX)
public class JobsProperties {

    public static final String PREFIX = "peach-job";

    /**
     * admin 访问token
     */
    private String adminAccessToken;

    /**
     * Jobs admin address, such as "http://address" or "http://address01,http://address02"
     */
    private String adminAddress;

    /**
     * APP 服务名
     */
    private String appName;

    /**
     * APP IP 地址
     */
    private String appIp;
    /**
     * APP 端口
     */
    private int appPort;

    /**
     * APP 访问token
     */
    private String appAccessToken;

    /**
     * 任务查询时间间隔
     */
    private int taskQueryInterval = 5;

    /**
     * 查询已经发送但没有反馈的任务的时间间隔
     * 一般要要于上面的时间间隔
     */
    private int noFeedBackTaskQueryInterval = 3;

}
