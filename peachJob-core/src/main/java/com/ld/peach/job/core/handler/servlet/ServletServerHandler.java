package com.ld.peach.job.core.handler.servlet;

import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.exception.helper.ExceptionHelper;
import com.ld.peach.job.core.generic.PeachRpcRequest;
import com.ld.peach.job.core.generic.PeachRpcResponse;
import com.ld.peach.job.core.rpc.RpcProviderFactory;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName ServletServerHandler
 * @Description Servlet Handler
 * @Author lidong
 * @Date 2020/10/20
 * @Version 1.0
 */
@Slf4j
public class ServletServerHandler {

    private final RpcProviderFactory rpcProviderFactory;

    public ServletServerHandler(RpcProviderFactory rpcProviderFactory) {
        this.rpcProviderFactory = rpcProviderFactory;
    }

    /**
     * handle servlet request
     */
    public void handle(String target, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ("/services".equals(target)) {

            StringBuffer stringBuffer = new StringBuffer("<ui>");
            for (String serviceKey : rpcProviderFactory.getServiceData().keySet()) {
                stringBuffer.append("<li>").append(serviceKey).append(": ").append(rpcProviderFactory.getServiceData().get(serviceKey)).append("</li>");
            }
            stringBuffer.append("</ui>");

            writeResponse(response, stringBuffer.toString().getBytes());
        } else {
            PeachRpcRequest rpcRequest;

            try {
                rpcRequest = parseRequest(request);
            } catch (Exception e) {
                writeResponse(response, ExceptionHelper.getErrorInfo(e).getBytes());
                return;
            }

            // invoke
            PeachRpcResponse rpcResponse = rpcProviderFactory.invokeService(rpcRequest);

            // response-serialize + response-write
            byte[] responseBytes = rpcProviderFactory.getSerializer().serialize(rpcResponse);
            writeResponse(response, responseBytes);
        }
    }

    /**
     * write response
     */
    private void writeResponse(HttpServletResponse response, byte[] responseBytes) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        OutputStream out = response.getOutputStream();
        out.write(responseBytes);
        out.flush();
    }

    /**
     * parse request
     */
    private PeachRpcRequest parseRequest(HttpServletRequest request) throws Exception {
        // deserialize request
        byte[] requestBytes = readBytes(request);
        if (requestBytes == null || requestBytes.length == 0) {
            throw new PeachRpcException("peach-rpc request data is empty.");
        }
        return (PeachRpcRequest) rpcProviderFactory.getSerializer()
                .deserialize(requestBytes, PeachRpcRequest.class);
    }

    /**
     * read bytes from http request
     */
    public static byte[] readBytes(HttpServletRequest request) throws IOException {
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
