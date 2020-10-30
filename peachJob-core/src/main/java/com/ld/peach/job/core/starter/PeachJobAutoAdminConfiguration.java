package com.ld.peach.job.core.starter;

import com.ld.peach.job.core.disruptor.TaskDisruptorTemplate;
import com.ld.peach.job.core.disruptor.TaskEvent;
import com.ld.peach.job.core.disruptor.TaskEventHandler;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private static final int WORKER_SIZE = Runtime.getRuntime().availableProcessors();

    @Bean
    @ConditionalOnMissingBean
    public IPeachJobRpcSerializer rpcSerializer() {
        return new HessianSerializer();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public TaskEventHandler taskEventHandler() {
        return new TaskEventHandler();
    }

    @Bean
    public TaskDisruptorTemplate taskDisruptorTemplate() {
        return new TaskDisruptorTemplate();
    }

    @Bean
    @ConditionalOnClass({RingBuffer.class})
    public RingBuffer<TaskEvent> disruptorRingBuffer() {

        RingBuffer<TaskEvent> ringBuffer = RingBuffer.create(ProducerType.MULTI, TaskEvent::new, 256 * 1024, new BlockingWaitStrategy());

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        ThreadPoolExecutor workerExecutor = new ThreadPoolExecutor(WORKER_SIZE, WORKER_SIZE * 2, 1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(1000), new ThreadFactory() {
            private int counter = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "DisruptorWorker-" + counter++);
            }
        });

        //初始化消费者
        WorkHandler<TaskEvent>[] consumers = new TaskEventHandler[WORKER_SIZE];
        for (int i = 0; i < WORKER_SIZE; i++) {
            consumers[i] = new TaskEventHandler();
        }

        WorkerPool<TaskEvent> workerPool = new WorkerPool<>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), consumers);
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(workerExecutor);


        // WEB 容器关闭执行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                workerPool.drainAndHalt();
            } catch (Exception e) {
                // to do nothing
            }
        }));


        return ringBuffer;
    }
}
