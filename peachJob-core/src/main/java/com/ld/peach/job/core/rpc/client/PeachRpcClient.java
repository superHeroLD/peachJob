package com.ld.peach.job.core.rpc.client;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.rpc.Client;
import com.ld.peach.job.core.rpc.common.ConnectClient;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;

import java.util.Objects;

/**
 * @ClassName PeachRpcClient
 * @Description RPC client
 * @Author lidong
 * @Date 2020/9/25
 * @Version 1.0
 */
public class PeachRpcClient extends Client {

    private final Class<? extends ConnectClient> connectClientImpl = PeachConnectClient.class;

    public PeachRpcClient(RpcReferenceBean rpcReferenceBean) {
        if (Objects.isNull(rpcReferenceBean)) {
            throw new PeachRpcException("peach-rpc init PeachRpcClient [rpcReferenceBean] is null");
        }
        super.rpcReferenceBean = rpcReferenceBean;
    }

    @Override
    public void asyncSend(String address, PeachRpcRequest rpcRequest) throws Exception {
        ConnectClient.asyncSend(rpcRequest, address, connectClientImpl, rpcReferenceBean);
    }
}
