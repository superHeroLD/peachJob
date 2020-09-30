package com.ld.peach.job.core.model.params;

import com.ld.peach.job.core.constant.RegisterStatusEnum;

import java.io.Serializable;

/**
 * @ClassName RegistryParam
 * @Description 注册参数
 * @Author lidong
 * @Date 2020/9/28
 * @Version 1.0
 */
public class RegistryParam implements Serializable {

    private RegisterStatusEnum registerStatusEnum = RegisterStatusEnum.ENABLED;
    private String app;
    private String address;

    public RegistryParam() {
    }

    public RegistryParam(String app, String address) {
        this.app = app;
        this.address = address;
    }

    public RegistryParam(RegisterStatusEnum registerStatusEnum, String app, String address) {
        this.registerStatusEnum = registerStatusEnum;
        this.app = app;
        this.address = address;
    }
}
