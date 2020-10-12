package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.executor.TaskSpringExecutor;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName PeachJobAutoConfiguration
 * @Description spring 自动装配
 * @Author lidong
 * @Date 2020/10/12
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(JobsProperties.class)
public class PeachJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IPeachJobRpcSerializer rpcSerializer() {
        return new HessianSerializer();
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public TaskSpringExecutor taskSpringExecutor(JobsProperties jobsProperties) {
        TaskSpringExecutor taskSpringExecutor = new TaskSpringExecutor();
        taskSpringExecutor.setAccessToken(jobsProperties.getAdminAccessToken());
        taskSpringExecutor.setAdminAddress(jobsProperties.getAdminAddress());
        taskSpringExecutor.setApp(jobsProperties.getAppName());
        taskSpringExecutor.setIp(jobsProperties.getAppIp());
        taskSpringExecutor.setPort(jobsProperties.getAppPort());
        return taskSpringExecutor;
    }
}
