package com.ld.peach.job.core.service;

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

    private static PeachJobHelper PEACH_JOB_HELPER = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        PEACH_JOB_HELPER = this;
    }

    @Resource
    private IAppService appService;

    /**
     * 获取 appService
     *
     * @return IAppService 实现实例，如果spring中没有会报错
     */
    public static IAppService getAppService() {
        return PEACH_JOB_HELPER.appService;
    }

}
