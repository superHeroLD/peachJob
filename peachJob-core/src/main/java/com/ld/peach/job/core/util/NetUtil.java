package com.ld.peach.job.core.util;

import com.ld.peach.job.core.exception.PeachRpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @ClassName NetUtil
 * @Description 网络相关工具类
 * @Author lidong
 * @Date 2020/9/16
 * @Version 1.0
 */
@Slf4j
public class NetUtil {

    /**
     * find avaliable port
     */
    public static int findAvailablePort(int defaultPort) {
        int portTmp = defaultPort;
        while (portTmp < 65535) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = defaultPort--;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new PeachRpcException("no available port.");
    }

    /**
     * check port used
     */
    public static boolean isPortUsed(int port) {
        boolean used;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            used = false;
        } catch (IOException e) {
            log.info("peach-rpc, port[{}] is in use.", port);
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.info("");
                }
            }
        }
        return used;
    }

}
