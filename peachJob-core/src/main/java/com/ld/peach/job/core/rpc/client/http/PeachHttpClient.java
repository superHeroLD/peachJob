package com.ld.peach.job.core.rpc.client.http;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.rpc.Client;
import com.ld.peach.job.core.rpc.common.ConnectClient;
import com.ld.peach.job.core.rpc.invoker.reference.RpcReferenceBean;

import java.util.Objects;

/**
 * @ClassName PeachHttpClient
 * @Description Http client
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
public class PeachHttpClient extends Client {

    private final Class<? extends ConnectClient> connectClientImpl = PeachHttpConnectClient.class;

    public PeachHttpClient() {
    }

    public PeachHttpClient(RpcReferenceBean rpcReferenceBean) {
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
