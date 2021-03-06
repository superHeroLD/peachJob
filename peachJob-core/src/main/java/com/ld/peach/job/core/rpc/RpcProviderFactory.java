package com.ld.peach.job.core.rpc;

import com.ld.peach.job.core.constant.ServerTypeEnum;
import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.exception.helper.ExceptionHelper;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.registry.IServiceRegistry;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.util.IpUtil;
import com.ld.peach.job.core.util.NetUtil;
import com.ld.peach.job.core.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.ld.peach.job.core.constant.TaskConstant.THREE_MINS_MILLIS;

/**
 * @ClassName RpcProviderFactory
 * @Description RPC provider 实现工厂
 * @Author lidong
 * @Date 2020/9/15
 * @Version 1.0
 */
@Data
@Slf4j
public class RpcProviderFactory {

    private String ip;

    private int port;

    /**
     * token
     */
    private String accessToken;

    /**
     * 序列化
     */
    private IPeachJobRpcSerializer serializer;

    private Class<? extends IServiceRegistry> serviceRegistryClass;

    private Map<String, String> serviceRegistryParam;

    private ServerTypeEnum serverTypeEnum;


    /**
     * 初始化配置
     *
     * @param serializer           序列化
     * @param serverTypeEnum       服务器类型
     * @param ip                   ip地址
     * @param port                 端口
     * @param accessToken          接入token
     * @param serviceRegistryClass 服务注册类
     * @param serviceRegistryParam 注册类启动参数
     */
    public void initConfig(IPeachJobRpcSerializer serializer,
                           ServerTypeEnum serverTypeEnum,
                           String ip,
                           int port,
                           String accessToken,
                           Class<? extends IServiceRegistry> serviceRegistryClass,
                           Map<String, String> serviceRegistryParam) {

        // init
        this.serializer = serializer;
        this.ip = ip;
        this.port = port;
        this.accessToken = accessToken;
        this.serviceRegistryClass = serviceRegistryClass;
        this.serviceRegistryParam = serviceRegistryParam;
        this.serverTypeEnum = serverTypeEnum;

        // valid
        if (Objects.isNull(this.serializer)) {
            throw new PeachRpcException("peach-rpc provider serializer is missing");
        }

        if (Objects.isNull(this.serverTypeEnum)) {
            throw new PeachRpcException("peach-rpc server type is missing");
        }

        if (StringUtil.isNotBlank(ip)) {
            this.ip = IpUtil.getIp();
        }

        if (this.port <= 0) {
            this.port = 9636;
        }

        if (NetUtil.isPortUsed(this.port)) {
            throw new PeachRpcException("peach-rpc provider port[" + this.port + "] is used.");
        }

        if (Objects.nonNull(this.serviceRegistryClass)) {
            if (Objects.isNull(this.serviceRegistryParam)) {
                throw new PeachRpcException("peach-rpc provider serviceRegistryParam is missing.");
            }
        }
    }


    //----------------------------------------RPC server----------------------------------------------

    private Server server;
    private IServiceRegistry serviceRegistry;
    private String serviceAddress;

    /**
     * stop rpc server
     *
     * @throws Exception exception
     */
    public void stop() throws Exception {
        // stop server
        server.stop();
    }

    /**
     * start rpc server
     *
     * @throws Exception exception
     */
    public void start() throws Exception {
        // start server
        serviceAddress = IpUtil.getIpPort(this.ip, port);

        //设置服务器类型
        server = serverTypeEnum.getServerClass().newInstance();

        //注册启动回调
        server.setStartedCallback(() -> {
            // start registry
            if (Objects.nonNull(serviceRegistryClass)) {
                try {
                    serviceRegistry = serviceRegistryClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                serviceRegistry.start(serviceRegistryParam);
                if (serviceData.size() > 0) {
                    serviceRegistry.registry(serviceData.keySet(), serviceAddress);
                }
            }
        });

        server.setStoppedCallback(() -> {
            // stop registry
            if (Objects.nonNull(serviceRegistry)) {
                if (serviceData.size() > 0) {
                    serviceRegistry.remove(serviceData.keySet(), serviceAddress);
                }
                serviceRegistry.stop();
                serviceRegistry = null;
            }
        });

        server.start(this);
    }


    /**
     * init local rpc service map
     */
    private Map<String, Object> serviceData = new ConcurrentHashMap<>();

    public Map<String, Object> getServiceData() {
        return serviceData;
    }

    /**
     * make service key
     *
     * @param iface   service interface name
     * @param version interface version
     * @return
     */
    public static String makeServiceKey(String iface, String version) {
        String serviceKey = iface;
        if (version != null && version.trim().length() > 0) {
            serviceKey += "#".concat(version);
        }
        return serviceKey;
    }

    /**
     * add service
     */
    public void addService(String iface, String version, Object serviceBean) {
        String serviceKey = makeServiceKey(iface, version);
        serviceData.put(serviceKey, serviceBean);
        log.info("peach-rpc provider factory add service success. serviceKey: [{}], serviceBean: [{}] version: [{}]", serviceKey, serviceBean.getClass(), version);
    }

    /**
     * 调用本地服务
     */
    public PeachRpcResponse invokeService(PeachRpcRequest jobsRpcRequest) {
        //  make response
        PeachRpcResponse jobsRpcResponse = new PeachRpcResponse();
        jobsRpcResponse.setRequestId(jobsRpcRequest.getRequestId());

        // match service bean
        String serviceKey = makeServiceKey(jobsRpcRequest.getClassName(), jobsRpcRequest.getVersion());
        Object serviceBean = serviceData.get(serviceKey);

        // valid
        if (Objects.isNull(serviceBean)) {
            jobsRpcResponse.setErrorMsg("The serviceKey[" + serviceKey + "] not found.");
            return jobsRpcResponse;
        }

        //时间戳校验
        if (System.currentTimeMillis() - jobsRpcRequest.getCreateMillisTime() > THREE_MINS_MILLIS) {
            jobsRpcResponse.setErrorMsg("The timestamp difference between admin and executor exceeds the limit.");
            return jobsRpcResponse;
        }

        if (StringUtil.isNotBlank(accessToken) && !accessToken.trim().equals(jobsRpcRequest.getAccessToken())) {
            jobsRpcResponse.setErrorMsg("The access token[" + jobsRpcRequest.getAccessToken() + "] is wrong.");
            return jobsRpcResponse;
        }

        try {
            // invoke
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = jobsRpcRequest.getMethodName();
            Class<?>[] parameterTypes = jobsRpcRequest.getParameterTypes();
            Object[] parameters = jobsRpcRequest.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);
            jobsRpcResponse.setResult(result);
        } catch (Throwable t) {
            log.error("peach rpc provider invokeService error", t);
            jobsRpcResponse.setErrorMsg(ExceptionHelper.getErrorInfo(t));
        }
        return jobsRpcResponse;
    }
}
