package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName PeachJobAutoAdminConfig
 * @Description 管理端
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(JobsProperties.class)
public class PeachJobAutoAdminConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IPeachJobRpcSerializer rpcSerializer() {
        return new HessianSerializer();
    }


}
