package com.ld.peach.job.admin;

import com.ld.peach.job.core.starter.TaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AdminConfig
 * @Description Admin 启动配置
 * @Author lidong
 * @Date 2020/10/20
 * @Version 1.0
 */
@Configuration
public class AdminConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskScheduler();
    }
}
