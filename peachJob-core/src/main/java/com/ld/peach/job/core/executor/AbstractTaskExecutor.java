package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.handler.ITaskHandler;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.invoker.call.CallType;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;
import com.ld.peach.job.core.rpc.registry.ExecutorRegistryThread;
import com.ld.peach.job.core.rpc.registry.IServiceRegistry;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.service.ITaskService;
import com.ld.peach.job.core.util.IpUtil;
import com.ld.peach.job.core.util.NetUtil;
import com.ld.peach.job.core.util.StringUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AbstractTaskExecutor
 * @Description Executor 父类
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Data
public abstract class AbstractTaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskExecutor.class);

    /**
     * jobs admin address, such as "http://address" or "http://address01,http://address02"
     */
    private String adminAddress;

    /**
     * 服务 APP
     */
    private String app;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 访问 Token
     */
    private String accessToken;

    /**
     * 序列化接口
     */
    public abstract IPeachJobRpcSerializer getRpcSerializer();

    /**
     * 启动
     *
     * @throws Exception Exception
     */
    public void start() throws Exception {
        // init invoker, admin-client
        initJobsAdminList(adminAddress, accessToken);

        // init executor-server
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (StringUtil.isNotBlank(ip)) ? ip : IpUtil.getIp();
        initRpcProvider(ip, port, app, accessToken);
    }

    /**
     * 销毁
     */
    public void destroy() {
        TASKS_HANDLER.clear();

        stopRpcProvider();

        stopInvokerFactory();
    }

    private void stopRpcProvider() {
        try {
            rpcProviderFactory.stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void stopInvokerFactory() {
        // stop invoker factory
        try {
            PeachRpcInvokerFactory.getInstance().stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Jobs Admin
     */
    private static List<ITaskService> TASK_SERVICE;

    public static List<ITaskService> getTaskServiceList() {
        return TASK_SERVICE;
    }

    private void initJobsAdminList(String adminAddress, String accessToken) throws Exception {
        if (StringUtil.isNotBlank(adminAddress)) {
            if (Objects.isNull(TASK_SERVICE)) {
                TASK_SERVICE = new ArrayList<>();
            }
            String[] addressArr = adminAddress.trim().split(TaskConstant.COMMA);
            for (String address : addressArr) {
                if (StringUtil.isNotBlank(address)) {
                    String addressUrl = address.concat(TaskConstant.TASK_API);
                    ITaskService jobsAdmin = (ITaskService) new RpcReferenceBean(
                            getRpcSerializer(),
                            CallType.SYNC,
                            ITaskService.class,
                            null,
                            10000,
                            addressUrl,
                            accessToken,
                            null,
                            null
                    ).getObject();

                    TASK_SERVICE.add(jobsAdmin);
                }
            }
        }
    }


    /**
     * rpc provider factory
     */
    private RpcProviderFactory rpcProviderFactory = null;

    private void initRpcProvider(String ip, int port, String appName, String accessToken) throws Exception {
        // init, provider factory
        Map<String, String> serviceRegistryParam = new HashMap<>(16);
        serviceRegistryParam.put("appName", appName);
        serviceRegistryParam.put("address", IpUtil.getIpPort(ip, port));

        rpcProviderFactory = new RpcProviderFactory();
        rpcProviderFactory.initConfig(getRpcSerializer(), ip, port, accessToken, ExecutorServiceRegistry.class, serviceRegistryParam);

        // add services
        rpcProviderFactory.addService(ITaskExecutor.class.getName(), null, new TaskExecutor());

        // start
        rpcProviderFactory.start();
    }

    /**
     * RPC Client 节点注册
     */
    public static class ExecutorServiceRegistry implements IServiceRegistry {

        @Override
        public void start(Map<String, String> param) {
            ExecutorRegistryThread.getInstance().start(param.get("appName"), param.get("address"));
        }

        @Override
        public void stop() {
            ExecutorRegistryThread.getInstance().stop();
        }

        @Override
        public boolean registry(Set<String> keys, String value) {
            return false;
        }

        @Override
        public boolean remove(Set<String> keys, String value) {
            return false;
        }

        @Override
        public Map<String, TreeSet<String>> discovery(Set<String> keys) {
            return null;
        }

        @Override
        public TreeSet<String> discovery(String key) {
            return null;
        }

    }


    /**
     * jobsHandler cache
     */
    private static Map<String, ITaskHandler> TASKS_HANDLER = new ConcurrentHashMap<>();

    public static ITaskHandler putTaskHandler(String name, ITaskHandler jobHandler) {
        LOGGER.info("tasks handler register success, name:{}", name);
        return TASKS_HANDLER.put(name, jobHandler);
    }

    public static ITaskHandler getTaskHandler(String name) {
        return TASKS_HANDLER.get(name);
    }
}
