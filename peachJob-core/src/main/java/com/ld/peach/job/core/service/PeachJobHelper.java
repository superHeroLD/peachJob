package com.ld.peach.job.core.service;

import com.ld.peach.job.core.disruptor.TaskDisruptorTemplate;
import com.ld.peach.job.core.starter.JobsProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @ClassName PeachJobHelper
 * @Description PeachJob 辅助工具类
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Configuration
public class PeachJobHelper implements InitializingBean {

    @Resource
    private IAppService appService;
    @Resource
    private JobsProperties jobsProperties;
    @Resource
    private TaskDisruptorTemplate taskDisruptorTemplate;

    private static PeachJobHelper PEACH_JOB_HELPER = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        PEACH_JOB_HELPER = this;
    }


    /**
     * 获取 appService
     *
     * @return IAppService 实现实例，如果spring中没有会报错
     */
    public static IAppService getAppService() {
        return PEACH_JOB_HELPER.appService;
    }

    /**
     * 获取配置信息
     *
     * @return 配置信息
     */
    public static JobsProperties getJobsProperties() {
        return PEACH_JOB_HELPER.jobsProperties;
    }

    /**
     * 获取Disruptor 操作模版
     *
     * @return Disruptor 操作模版
     */
    public static TaskDisruptorTemplate getTaskDisruptorTemplate() {
        return PEACH_JOB_HELPER.taskDisruptorTemplate;
    }

}
