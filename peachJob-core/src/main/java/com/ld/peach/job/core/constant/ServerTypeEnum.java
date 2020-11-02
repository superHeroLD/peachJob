package com.ld.peach.job.core.constant;

import com.ld.peach.job.core.rpc.Client;
import com.ld.peach.job.core.rpc.Server;
import com.ld.peach.job.core.rpc.client.http.PeachHttpClient;
import com.ld.peach.job.core.rpc.client.socket.PeachRpcClient;
import com.ld.peach.job.core.rpc.server.PeachHttpServer;
import com.ld.peach.job.core.rpc.server.PeachRpcServer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ServerTypeEnum
 * @Description 服务器类型
 * @Author lidong
 * @Date 2020/11/2
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum ServerTypeEnum {

    /**
     * socket
     */
    NETTY(PeachRpcServer.class, PeachRpcClient.class),

    /**
     * http
     */
    NETTY_HTTP(PeachHttpServer.class, PeachHttpClient.class);

    public final Class<? extends Server> serverClass;
    public final Class<? extends Client> clientClass;
}
