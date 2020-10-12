package com.ld.peach.job.core.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @InterfaceName EnablePeachJob
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/12
 * @Version 1.0
 */
@Configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({PeachJobAutoConfiguration.class})
@ConditionalOnProperty(prefix = JobsProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface EnablePeachJob {
}
