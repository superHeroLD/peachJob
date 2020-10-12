package com.ld.peach.job.core.model.params;

import com.ld.peach.job.core.constant.RegisterStatusEnum;

import java.io.Serializable;
import java.util.StringJoiner;

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

    public RegisterStatusEnum getRegisterStatusEnum() {
        return registerStatusEnum;
    }

    public void setRegisterStatusEnum(RegisterStatusEnum registerStatusEnum) {
        this.registerStatusEnum = registerStatusEnum;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RegistryParam.class.getSimpleName() + "[", "]")
                .add("registerStatusEnum=" + registerStatusEnum)
                .add("app='" + app + "'")
                .add("address='" + address + "'")
                .toString();
    }
}
