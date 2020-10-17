package com.ld.peach.job.admin.controller;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.serialize.impl.HessianSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName ApiController
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/17
 * @Version 1.0
 */
@Slf4j
@Controller
public class ApiController implements InitializingBean {

    HessianSerializer serializer;

    @Override
    public void afterPropertiesSet() throws Exception {
        serializer = new HessianSerializer();
    }

    @RequestMapping(TaskConstant.TASK_API)
    public void api(HttpServletRequest request, HttpServletResponse response) throws Exception {

        PeachRpcRequest rpcRequest = parseRequest(request);
        log.info("[ApiController] api receive rpc request : {}", rpcRequest);


        PeachRpcResponse rpcResponse = new PeachRpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setResult(true);

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        OutputStream out = response.getOutputStream();

        byte[] serialize = serializer.serialize(rpcResponse);
        log.info("[ApiController] serialize: {}", serialize);
        out.write(serialize);
        out.flush();
    }

    private PeachRpcRequest parseRequest(HttpServletRequest request) throws Exception {
        // deserialize request
        byte[] requestBytes = readBytes(request);
        if (requestBytes == null || requestBytes.length == 0) {
            throw new PeachRpcException("peach rpc request data is empty.");
        }
        return serializer.deserialize(requestBytes, PeachRpcRequest.class);
    }

    public byte[] readBytes(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        int contentLen = request.getContentLength();
        InputStream is = request.getInputStream();
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return message;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return new byte[]{};
    }
}
