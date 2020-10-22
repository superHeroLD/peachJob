package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.service.PeachJobHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动PeachJobAdmin 注解
 *
 * @author lidong
 */
@Configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({PeachJobHelper.class, TaskScheduler.class, PeachJobAutoAdminConfiguration.class})
@ConditionalOnProperty(prefix = JobsProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface EnablePeachJobAdmin {
}
