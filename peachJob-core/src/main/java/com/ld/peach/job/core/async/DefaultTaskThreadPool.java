package com.ld.peach.job.core.async;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

/**
 * @ClassName DefaultTaskThreadPool
 * @Description 默认执行任务线程池
 * @Author lidong
 * @Date 2020/4/28
 * @Version 1.0
 */
public class DefaultTaskThreadPool {

    /**
     * 线程池核心线程数
     */
    protected static final int CORE_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 默认任务队列长度
     */
    protected static final int DEFAULT_TASK_QUEUE_SIZE = 500;

    private DefaultTaskThreadPool() {
    }

    private static class ThreadPoolHolder {
        private static final ThreadPoolExecutor INSTANCE = new ThreadPoolExecutor(CORE_NUM, CORE_NUM,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(DEFAULT_TASK_QUEUE_SIZE),
                new DefaultTaskThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    static class DefaultTaskThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultTaskThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "DefaultTaskThreadPool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public static ThreadPoolExecutor getInstance() {
        return ThreadPoolHolder.INSTANCE;
    }

    public static Future async(Runnable task) {
        return getInstance().submit(task);
    }

    public static <P> Future async(Consumer<P> method, P param) {
        return getInstance().submit(() -> method.accept(param));
    }

    public static <P1, P2> Future async(BiConsumer<P1, P2> method, P1 param1, P2 param2) {
        return getInstance().submit(() -> method.accept(param1, param2));
    }

    public static <R> Future<R> async(Supplier<R> method) {
        return getInstance().submit(method::get);
    }


    public static <P, R> Future<R> async(Function<P, R> method, P param) {
        return getInstance().submit(() -> method.apply(param));
    }

    public static <P1, P2, R> Future<R> async(BiFunction<P1, P2, R> method, P1 param1, P2 param2) {
        return getInstance().submit(() -> method.apply(param1, param2));
    }
}
