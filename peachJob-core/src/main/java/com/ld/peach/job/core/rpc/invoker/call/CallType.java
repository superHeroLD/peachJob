package com.ld.peach.job.core.rpc.invoker.call;

/**
 * @EnumName CallType
 * @Description 请求类型
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public enum CallType {
    /**
     * 请求类型
     */
    SYNC("同步"),
    FUTURE("异步"),
    CALLBACK("回调"),
    ONEWAY("单向请求");

    CallType(String desc) {
        this.desc = desc;
    }

    private final String desc;

    public String getDesc() {
        return desc;
    }
}
