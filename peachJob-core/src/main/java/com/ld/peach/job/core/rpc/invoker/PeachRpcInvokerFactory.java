package com.ld.peach.job.core.rpc.invoker;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcFutureResponse;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.IRpcCallBack;
import com.ld.peach.job.core.rpc.registry.IServiceRegistry;
import com.ld.peach.job.core.rpc.registry.impl.LocalServiceRegistry;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @ClassName PeachRpcInvokerFactory
 * @Description 初始化服务注册信息
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
@Slf4j
public class PeachRpcInvokerFactory {

    private final Class<? extends IServiceRegistry> serviceRegistryClass;
    private final Map<String, String> serviceRegistryParam;

    public PeachRpcInvokerFactory(Class<? extends IServiceRegistry> serviceRegistryClass, Map<String, String> serviceRegistryParam) {
        this.serviceRegistryClass = serviceRegistryClass;
        this.serviceRegistryParam = serviceRegistryParam;
    }

    private static final PeachRpcInvokerFactory INSTANCE = new PeachRpcInvokerFactory(LocalServiceRegistry.class, null);

    /**
     * 获取实例
     *
     * @return PeachRpcInvokerFactory
     */
    public static PeachRpcInvokerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 服务注册实例
     */
    private IServiceRegistry serviceRegistry;

    public IServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void start() throws Exception {
        serviceRegistry = Objects.requireNonNull(serviceRegistryClass, "[PeachRpcInvokerFactory] missing serviceRegistryClass").newInstance();
        serviceRegistry.start(serviceRegistryParam);
    }

    public void stop() throws Exception {
        // stop registry
        if (Objects.nonNull(serviceRegistry)) {
            serviceRegistry.stop();
        }

        stopCallbackThreadPool();
    }


    private List<IRpcCallBack> stopCallbackList = new ArrayList<>();

    public void addStopCallBack(IRpcCallBack callback) {
        stopCallbackList.add(callback);
    }

    private ConcurrentMap<String, PeachRpcFutureResponse> futureResponsePool = new ConcurrentHashMap<>();

    public void setInvokerFuture(String requestId, PeachRpcFutureResponse futureResponse) {
        futureResponsePool.put(requestId, futureResponse);
    }

    public void removeInvokerFuture(String requestId) {
        futureResponsePool.remove(requestId);
    }

    public void notifyInvokerFuture(String requestId, final PeachRpcResponse peachRpcResponse) {
        final PeachRpcFutureResponse futureResponse = futureResponsePool.get(requestId);

        if (Objects.isNull(futureResponse)) {
            return;
        }

        if (futureResponse.getInvokeCallback() != null) {
            try {
                executeResponseCallback(() -> {
                    if (StringUtil.isNotBlank(peachRpcResponse.getErrorMsg())) {
                        futureResponse.getInvokeCallback().onFailure(new PeachRpcException(peachRpcResponse.getErrorMsg()));
                    } else {
                        futureResponse.getInvokeCallback().onSuccess(peachRpcResponse.getResult());
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            futureResponse.setResponse(peachRpcResponse);
        }

        futureResponsePool.remove(requestId);
    }

    /**
     * response callback ThreadPool
     */
    private volatile static ThreadPoolExecutor responseCallbackThreadPool;

    public void executeResponseCallback(Runnable runnable) {
        if (Objects.isNull(responseCallbackThreadPool)) {
            synchronized (PeachRpcInvokerFactory.class) {
                if (Objects.isNull(responseCallbackThreadPool)) {
                    responseCallbackThreadPool = new ThreadPoolExecutor(
                            10,
                            50,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(1000),
                            r -> new Thread(r, "[PeachRpcInvokerFactory]-responseCallbackThreadPool-" + r.hashCode()),
                            (r, executor) -> {
                                throw new PeachRpcException("peach-rpc Invoke Callback Thread pool is EXHAUSTED!");
                            });
                }
            }
        }

        responseCallbackThreadPool.execute(runnable);
    }

    public void stopCallbackThreadPool() {
        if (Objects.nonNull(responseCallbackThreadPool)) {
            responseCallbackThreadPool.shutdown();
        }
    }
}
