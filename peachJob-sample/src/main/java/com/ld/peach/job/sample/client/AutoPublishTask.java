package com.ld.peach.job.sample.client;

import com.ld.peach.job.core.client.PeachJobClient;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.builder.PeachTaskBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AutoPublishTask
 * @Description 自动发布任务
 * @Author lidong
 * @Date 2020/10/27
 * @Version 1.0
 */
@Slf4j
@Configuration
public class AutoPublishTask implements InitializingBean, DisposableBean {

    private ScheduledExecutorService executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = new ScheduledThreadPoolExecutor(4);

        executor.scheduleAtFixedRate(() -> {
            try {
                boolean result = PeachJobClient.publishTask(buildTestTask());

                if (!result) {
                    log.error("send task fail");
                }
            } catch (Exception e) {
                log.error("send test task occur error: ", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (Objects.nonNull(executor)) {
            executor.shutdown();
        }
    }

    /**
     * 构造测试任务
     *
     * @return 任务信息
     */
    private TaskInfo buildTestTask() {
        return PeachTaskBuilder.newBuilder()
                .taskHandler("TestTaskHandler")
                .taskName("TestTask")
                .executeParams("This is a test task")
                .executionDate(ThreadLocalRandom.current().nextInt(10, 100), TimeUnit.SECONDS)
                .build();
    }

}
