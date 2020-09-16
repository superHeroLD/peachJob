package com.ld.peach.job.core.rpc.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.ld.peach.job.core.exception.PeachRpcException;
import com.ld.peach.job.core.rpc.serialize.IPeachJobRpcSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName HessianSerializer
 * @Description Hessian 序列化
 * @Author lidong
 * @Date 2020/9/16
 * @Version 1.0
 */
public class HessianSerializer implements IPeachJobRpcSerializer {

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
        try {
            hessian2Output.writeObject(obj);

            // 必须先关闭，才能转成二进制数组
            hessian2Output.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new PeachRpcException(e);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw new PeachRpcException(e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);
        try {
            return (T) hessian2Input.readObject(clazz);
        } catch (IOException e) {
            throw new PeachRpcException(e);
        } finally {
            try {
                hessian2Input.close();
                byteArrayInputStream.close();
            } catch (IOException e) {
                throw new PeachRpcException(e);
            }
        }
    }
}
