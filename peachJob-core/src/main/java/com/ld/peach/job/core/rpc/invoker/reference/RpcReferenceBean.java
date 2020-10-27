package com.ld.peach.job.core.rpc.invoker.reference;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcFutureResponse;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.Client;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import com.ld.peach.job.core.rpc.client.http.PeachHttpClient;
import com.ld.peach.job.core.rpc.invoker.PeachRpcInvokerFactory;
import com.ld.peach.job.core.rpc.invoker.call.CallType;
import com.ld.peach.job.core.rpc.invoker.call.PeachRpcInvokeCallback;
import com.ld.peach.job.core.rpc.invoker.call.PeachRpcInvokeFuture;
import com.ld.peach.job.core.rpc.invoker.common.PeachRpcGenericService;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;
import com.ld.peach.job.core.util.ClassUtil;
import com.ld.peach.job.core.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName RpcReferenceBean
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
@Slf4j
@Getter
public class RpcReferenceBean {

    /**
     * 序列化协议 实现
     */
    private final IPeachJobRpcSerializer serializer;

    /**
     * 请求类型
     */
    private CallType callType;

    /**
     * 调用接口信息
     */
    private Class<?> iface;

    /**
     * 版本
     */
    private String version;

    /**
     * 超时时间
     */
    private long timeout = 1000;

    /**
     * 请求地址
     */
    private String address;

    /**
     * token
     */
    private String accessToken;

    private PeachRpcInvokeCallback invokeCallback;

    private PeachRpcInvokerFactory invokerFactory;


    public RpcReferenceBean(IPeachJobRpcSerializer serializer,
                            CallType callType,
                            Class<?> iface,
                            String version,
                            long timeout,
                            String address,
                            String accessToken,
                            PeachRpcInvokeCallback invokeCallback,
                            PeachRpcInvokerFactory invokerFactory) {


        this.serializer = serializer;
        this.callType = callType;
        this.iface = iface;
        this.version = version;
        this.timeout = timeout;
        this.address = address;
        this.accessToken = accessToken;
        this.invokeCallback = invokeCallback;
        this.invokerFactory = invokerFactory;

        if (Objects.isNull(this.serializer)) {
            throw new PeachRpcException("peach-rpc reference serializer is missing");
        }

        if (Objects.isNull(this.callType)) {
            throw new PeachRpcException("peach-rpc reference callType is missing");
        }

        if (Objects.isNull(this.iface)) {
            throw new PeachRpcException("peach-rpc reference iface is missing");
        }

        if (Objects.isNull(this.invokerFactory)) {
            this.invokerFactory = PeachRpcInvokerFactory.getInstance();
        }

        if (timeout < 0) {
            timeout = 0;
        }

        initClient();
    }


    /**
     * rpc 客户端
     */
    private Client client = null;

    /**
     * 初始化客户端
     */
    private void initClient() {
        try {
            client = new PeachHttpClient(this);
        } catch (Exception e) {
            throw new PeachRpcException(e);
        }
    }


    public Object getObject() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iface}, (proxy, method, args) -> {
            String className = method.getDeclaringClass().getName();
            String tmpVersion = version;
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = args;

            // filter for generic
            if (className.equals(PeachRpcGenericService.class.getName()) && "invoke".equals(methodName)) {
                Class<?>[] paramTypes = null;
                if (args[3] != null) {
                    String[] paramTypesStrArr = (String[]) args[3];
                    if (paramTypesStrArr.length > 0) {
                        paramTypes = new Class[paramTypesStrArr.length];
                        for (int i = 0; i < paramTypesStrArr.length; i++) {
                            paramTypes[i] = ClassUtil.resolveClass(paramTypesStrArr[i]);
                        }
                    }
                }

                className = (String) args[0];
                tmpVersion = (String) args[1];
                methodName = (String) args[2];
                parameterTypes = paramTypes;
                parameters = (Object[]) args[4];
            }

            // filter method like "Object.toString()"
            if (className.equals(Object.class.getName())) {
                log.info("peach-rpc proxy class-method not support [{}#{}]", className, methodName);
                throw new PeachRpcException("peach-rpc proxy class-method not support");
            }

            // address
            String finalAddress = address;
            //如果为空就是没有指定 就要初始化一下
            if (StringUtil.isBlank(finalAddress)) {
                if (invokerFactory != null && invokerFactory.getServiceRegistry() != null) {
                    // discovery
                    String serviceKey = RpcProviderFactory.makeServiceKey(className, tmpVersion);
                    TreeSet<String> addressSet = invokerFactory.getServiceRegistry().discovery(serviceKey);
                    // load balance
                    if (addressSet == null || addressSet.size() == 0) {
                        // pass
                    } else if (addressSet.size() == 1) {
                        finalAddress = addressSet.first();
                    }

                    //TODO 这里要写负载算法

                }
            }

            if (StringUtil.isBlank(finalAddress)) {
                throw new PeachRpcException("peach-rpc reference bean[" + className + "] address empty");
            }

            // request
            PeachRpcRequest rpcRequest = new PeachRpcRequest();
            rpcRequest.setRequestId(UUID.randomUUID().toString());
            rpcRequest.setCreateMillisTime(System.currentTimeMillis());
            rpcRequest.setAccessToken(accessToken);
            rpcRequest.setClassName(className);
            rpcRequest.setMethodName(methodName);
            rpcRequest.setParameterTypes(parameterTypes);
            rpcRequest.setParameters(parameters);

            // send
            if (CallType.SYNC.equals(callType)) {
                PeachRpcFutureResponse futureResponse = new PeachRpcFutureResponse(invokerFactory, rpcRequest, null);

                try {
                    // do invoke
                    client.asyncSend(finalAddress, rpcRequest);

                    // future get
                    PeachRpcResponse rpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);

                    if (Objects.isNull(rpcResponse)) {
                        throw new PeachRpcException(String.format("no response from address: [%s]", finalAddress));
                    }

                    if (StringUtil.isNotBlank(rpcResponse.getErrorMsg())) {
                        throw new PeachRpcException(rpcResponse.getErrorMsg());
                    }

                    return rpcResponse.getResult();
                } catch (Exception e) {
                    log.error("peach-rpc invoke error, address: [{}] callType: [{}] RPC Request: {}", finalAddress, callType, rpcRequest);
                    throw (e instanceof PeachRpcException) ? e : new PeachRpcException(e);
                } finally {
                    futureResponse.removeInvokerFuture();
                }
            } else if (CallType.FUTURE.equals(callType)) {
                PeachRpcFutureResponse futureResponse = new PeachRpcFutureResponse(invokerFactory, rpcRequest, null);
                try {
                    // invoke future set
                    PeachRpcInvokeFuture invokeFuture = new PeachRpcInvokeFuture(futureResponse);
                    invokeFuture.setFuture(invokeFuture);

                    // do invoke
                    client.asyncSend(finalAddress, rpcRequest);

                    return null;
                } catch (Exception e) {
                    log.info("peach-rpc, invoke error, address: [{}] callType: [{}] RPC Request: {}", finalAddress, callType, rpcRequest);

                    // future-response remove
                    futureResponse.removeInvokerFuture();
                    throw (e instanceof PeachRpcException) ? e : new PeachRpcException(e);
                }
            } else if (CallType.CALLBACK.equals(callType)) {
                PeachRpcInvokeCallback finalInvokeCallback = invokeCallback;
                PeachRpcInvokeCallback threadInvokeCallback = PeachRpcInvokeCallback.getCallback();

                if (threadInvokeCallback != null) {
                    finalInvokeCallback = threadInvokeCallback;
                }
                if (finalInvokeCallback == null) {
                    throw new PeachRpcException("peach-rpc RpcInvokeCallback（CallType=" + CallType.CALLBACK.name() + "） cannot be null.");
                }

                // future-response set
                PeachRpcFutureResponse futureResponse = new PeachRpcFutureResponse(invokerFactory, rpcRequest, finalInvokeCallback);
                try {
                    client.asyncSend(finalAddress, rpcRequest);
                } catch (Exception e) {
                    log.info("peach-rpc, invoke error, address: [{}] callType: [{}] RPC Request: {}", finalAddress, callType, rpcRequest);

                    // future-response remove
                    futureResponse.removeInvokerFuture();
                    throw (e instanceof PeachRpcException) ? e : new PeachRpcException(e);
                }

                return null;
            } else if (CallType.ONE_WAY.equals(callType)) {
                client.asyncSend(finalAddress, rpcRequest);
                return null;
            } else {
                throw new PeachRpcException("peach-rpc callType[" + callType + "] invalid");
            }
        });
    }
}
