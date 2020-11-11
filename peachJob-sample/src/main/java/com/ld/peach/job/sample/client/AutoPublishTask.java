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
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final int SEND_SIZE = 100000;

    private ScheduledExecutorService executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = new ScheduledThreadPoolExecutor(4);

        AtomicInteger count = new AtomicInteger(0);

        executor.scheduleAtFixedRate(() -> {

            if (count.get() <= SEND_SIZE) {
                sendTestTask(count.getAndIncrement());
            }

        }, 10, 1, TimeUnit.MILLISECONDS);

        if (count.get() >= SEND_SIZE) {
            executor.shutdown();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (Objects.nonNull(executor)) {
            executor.shutdown();
        }
    }

    /**
     * 发送测试任务
     */
    private void sendTestTask(int serialNum) {
        try {
            boolean result = PeachJobClient.publishTask(buildTestTask(serialNum));

            if (!result) {
                log.error("send task fail");
            }
        } catch (Exception e) {
            log.error("send test task occur error: ", e);
        }
    }

    /**
     * 构造测试任务
     *
     * @return 任务信息
     */
    private TaskInfo buildTestTask(int serialNum) {
        return PeachTaskBuilder.newBuilder()
                .taskHandler("TestTaskHandler")
                .taskName("TestTask")
                .executeParams(String.valueOf(serialNum))
                .executionDate(ThreadLocalRandom.current().nextInt(10, 100), TimeUnit.SECONDS)
                .build();
    }
}
