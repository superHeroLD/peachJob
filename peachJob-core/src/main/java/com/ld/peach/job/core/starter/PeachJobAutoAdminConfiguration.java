package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.disruptor.TaskDisruptorTemplate;
import com.ld.peach.job.core.disruptor.TaskEvent;
import com.ld.peach.job.core.disruptor.TaskEventHandler;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Bean
    @ConditionalOnMissingBean
    public TaskEventHandler taskEventHandler() {
        return new TaskEventHandler();
    }

    @Bean
    public TaskDisruptorTemplate taskDisruptorTemplate() {
        return new TaskDisruptorTemplate();
    }

    @Bean
    @ConditionalOnClass({Disruptor.class})
    public Disruptor<TaskEvent> disruptor(TaskEventHandler taskEventHandler) {

        //disruptor 默认配置
        Disruptor<TaskEvent> disruptor = new Disruptor<>(TaskEvent::new, 256 * 1024,
                new ThreadFactory() {
                    final AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "Disruptor-Thread-[" + count.getAndIncrement() + "]");
                        t.setDaemon(true);
                        return t;
                    }
                }, ProducerType.MULTI, new SleepingWaitStrategy());

        disruptor.handleEventsWith(taskEventHandler);

        // 启动
        disruptor.start();

        // WEB 容器关闭执行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // OK
                disruptor.shutdown();

                // wait up to 10 seconds for the ringbuffer to drain
                RingBuffer<TaskEvent> ringBuffer = disruptor.getRingBuffer();
                for (int i = 0; i < 20; i++) {
                    if (ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize())) {
                        break;
                    }
                    try {
                        // give ringbuffer some time to drain...
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // ignored
                    }
                }
                disruptor.shutdown();
            } catch (Exception e) {
                // to do nothing
            }
        }));

        return disruptor;
    }
}
