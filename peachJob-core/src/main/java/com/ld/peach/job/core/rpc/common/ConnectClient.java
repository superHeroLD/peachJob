package com.ld.peach.job.core.rpc.common;

import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.util.MapUtil;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ConnectClient
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/25
 * @Version 1.0
 */
public abstract class ConnectClient {

    /**
     * 初始化客户端
     *
     * @param address                地址
     * @param serializer             序列化协议
     * @param peachRpcInvokerFactory 服务注册工厂
     * @throws Exception 异常
     */
    public abstract void init(String address, final IPeachJobRpcSerializer serializer, final PeachRpcInvokerFactory peachRpcInvokerFactory) throws Exception;

    /**
     * 关闭客户端
     */
    public abstract void close();

    /**
     * 客户端是否有效
     *
     * @return 是否有效
     */
    public abstract boolean isValidate();

    /**
     * 发送请求
     *
     * @param request 请求
     * @throws Exception 异常
     */
    public abstract void send(PeachRpcRequest request) throws Exception;

    /**
     * Map<address, ConnectClient></>
     */
    private static volatile ConcurrentHashMap<String, ConnectClient> connectClientMap;

    /**
     * Map<address, 锁></>
     */
    private static volatile ConcurrentHashMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();

    /**
     * 发送请求
     *
     * @param rpcRequest        rpc请求
     * @param address           服务地址
     * @param connectClientImpl 客户端实现
     * @param referenceBean     rpc ？？？？
     * @throws Exception Exception
     */
    public static void asyncSend(PeachRpcRequest rpcRequest, String address, Class<? extends ConnectClient> connectClientImpl,
                                 final RpcReferenceBean referenceBean) throws Exception {
        ConnectClient.getClient(address, connectClientImpl, referenceBean).send(rpcRequest);
    }

    /**
     * 获取rpc客户端
     *
     * @param address           客户端地址
     * @param connectClientImpl 客户端实现
     * @param rpcReferenceBean  ？？？？？？
     * @return 客户端实例
     * @throws Exception Exception
     */
    private static ConnectClient getClient(String address, Class<? extends ConnectClient> connectClientImpl, final RpcReferenceBean rpcReferenceBean) throws Exception {
        if (MapUtil.isEmpty(connectClientMap)) {
            synchronized (ConnectClient.class) {
                if (MapUtil.isEmpty(connectClientMap)) {
                    // init
                    connectClientMap = new ConcurrentHashMap<>(16);
                    // stop callback
                    rpcReferenceBean.getInvokerFactory().addStopCallBack(() -> {
                        if (connectClientMap.size() > 0) {
                            for (String key : connectClientMap.keySet()) {
                                ConnectClient clientPool = connectClientMap.get(key);
                                clientPool.close();
                            }
                            connectClientMap.clear();
                        }
                    });
                }
            }
        }

        // get-valid client
        ConnectClient connectClient = connectClientMap.get(address);
        if (Objects.nonNull(connectClient) && connectClient.isValidate()) {
            return connectClient;
        }

        // TODO 这里有问题吧
        Object clientLock = connectClientLockMap.get(address);
        if (Objects.isNull(clientLock)) {
            connectClientLockMap.putIfAbsent(address, new Object());
            clientLock = connectClientLockMap.get(address);
        }

        //这里？？？？？？
        synchronized (clientLock) {
            connectClient = connectClientMap.get(address);
            if (Objects.nonNull(connectClient) && connectClient.isValidate()) {
                return connectClient;
            }

            if (Objects.nonNull(connectClient)) {
                connectClient.close();
                connectClientMap.remove(address);
            }

            ConnectClient newConnectClient = connectClientImpl.newInstance();
            try {
                newConnectClient.init(address, rpcReferenceBean.getSerializer(), rpcReferenceBean.getInvokerFactory());
                connectClientMap.put(address, newConnectClient);
            } catch (Exception e) {
                newConnectClient.close();
                throw e;
            }

            return newConnectClient;
        }
    }
}
