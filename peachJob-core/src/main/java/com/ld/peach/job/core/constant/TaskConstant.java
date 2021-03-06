package com.ld.peach.job.core.constant;

/**
 * @InterfaceName JobConstant
 * @Description 常量
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public interface TaskConstant {
    /**
     * 成功
     */
    int CODE_SUCCESS = 1;

    /**
     * 失败
     */
    int CODE_FAILED = -1;

    /**
     * 心跳时长
     */
    int BEAT_TIMEOUT = 30;

    /**
     * 最大重试次数
     */
    int MAX_RETRY_NUM = 10;

    /**
     * 清理时长，需比心跳稍大
     */
    int CLEAN_TIMEOUT = 50000;

    /**
     * owner标志常量，用于标志是否做过tryLock()操作
     */
    String OPERATION_TRY_LOCK = "OPERATION_TRY_LOCK";

    /**
     * 锁唯一标示
     */
    String DEFAULT_LOCK_KEY = "PEACH_JOB_LOCK";

    /**
     * 异常任务锁
     */
    String ABNORMAL_TASK_LOCK_KEY = "ABNORMAL_TASK_LOCK";

    /**
     * 逗号分隔符
     */
    String COMMA = ",";

    /**
     * API URI
     */
    String ADMIN_SERVICE_API = "/adminService-api";

    /**
     * 三分钟毫秒
     */
    int THREE_MINS_MILLIS = 3 * 60 * 1000;
}
