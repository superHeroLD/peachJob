package com.ld.peach.job.core.service;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.params.RegistryParam;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @InterfaceName IJobService
 * @Description 一些常用的接口集合Service层
 * @Author lidong
 * @Date 2020/9/28
 * @Version 1.0
 */
public interface IAdminService {

    /**
     * 发布任务
     *
     * @param taskInfo 任务信息
     * @return 发布数量
     */
    int publishTask(TaskInfo taskInfo);

    /**
     * 批量发布任务信息
     *
     * @param taskInfoList 任务信息
     * @return 发布数量
     */
    int batchPublishTask(List<TaskInfo> taskInfoList);

    /**
     * 节点注册
     *
     * @param registryParam 注册参数
     * @return 是否注册成功
     */
    boolean registry(RegistryParam registryParam);

    /**
     * 查询任务列表
     * 时间间隔单位是分钟
     * 查询时间的字段是estimated_execution_time
     *
     * @param timeInterVal 时间间隔
     * @return 未执行任务列表
     */
    List<TaskInfo> getTaskListByCondition(int timeInterVal, List<TaskExecutionStatus> statusList);

    /**
     * 查询一段时间间隔之前的任务数据
     * 这里查询的时间是预期执行时间
     *
     * @param timeInterVal 时间间隔
     * @param statusList   状态列表
     * @return 任务列表
     */
    List<TaskInfo> getTaskListByEstimatedTimeCondition(int timeInterVal, List<TaskExecutionStatus> statusList);

    /**
     * 根据 任务ID 获取任务信息对象
     *
     * @param id 任务 ID
     * @return 获取任务信息
     */
    TaskInfo getTaskInfoById(Long id);

    /**
     * 根据 任务ID 更新任务信息
     *
     * @param taskInfo 任务信息对象
     * @return 是否更新成功
     */
    boolean updateTaskInfoById(TaskInfo taskInfo);

    /**
     * 批量更新 taskInfo
     *
     * @param taskInfoList 任务集合
     * @return 更新数量
     */
    int batchUpdateTaskInfoById(List<TaskInfo> taskInfoList);

    /**
     * 使用线程本地变量记录锁的持有者
     */
    ThreadLocal<String> OWNER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁 KEY
     * @return 返回true代表已经获得锁，false代表获取锁失败（锁已经被别的进程占有）
     */
    default boolean tryLock(String lockKey) {
        String owner = OWNER_THREAD_LOCAL.get();
        if (Objects.nonNull(owner) && !owner.equals(TaskConstant.OPERATION_TRY_LOCK)) {
            // already hold a lock
            return true;
        }
        OWNER_THREAD_LOCAL.set(TaskConstant.OPERATION_TRY_LOCK);

        owner = UUID.randomUUID().toString().replace("-", "")
                .concat(String.valueOf(ThreadLocalRandom.current().nextInt(123456)));

        if (tryLock(lockKey, owner)) {
            OWNER_THREAD_LOCAL.set(owner);
            return true;
        }
        return false;
    }


    /**
     * 释放锁
     *
     * @param lockKey 锁 KEY
     * @param force   强制解锁
     */
    default void unlock(String lockKey, boolean force) {
        if (force) {
            unlock(lockKey, null);
        } else {
            String owner = OWNER_THREAD_LOCAL.get();
            if (Objects.isNull(owner)) {
                throw new IllegalMonitorStateException("should not call unlock() without tryLock(()");
            }

            OWNER_THREAD_LOCAL.remove();
            if (!TaskConstant.OPERATION_TRY_LOCK.equals(owner)) {
                unlock(lockKey, owner);
            }
        }
    }

    /**
     * 插入一条记录，标志着占有锁
     *
     * @param name  锁的名称
     * @param owner 锁的持有者
     * @return 返回影响的记录行数
     */
    boolean tryLock(String name, String owner);

    /**
     * 释放锁
     *
     * @param name  锁的名称
     * @param owner 锁的持有者，不存在则根据 name 删除
     * @return 返回影响的记录行数
     */
    boolean unlock(String name, String owner);


    /**
     * 清理超时节点
     *
     * @return 清理数量
     */
    default int cleanTimeoutApp() {
        return removeTimeOutApp(TaskConstant.CLEAN_TIMEOUT);
    }

    /**
     * 移除超时节点
     *
     * @param timeout 超时时长
     */
    int removeTimeOutApp(int timeout);

    /**
     * 移除节点
     *
     * @param registryParam 注册参数
     */
    boolean removeApp(RegistryParam registryParam);


    /**
     * 查询注册地址列表
     * 只获取还维持心跳的
     *
     * @return 注册的服务端的IP和端口
     */
    List<String> getAppAddressList();

    /**
     * 记录任务日志
     *
     * @param taskInfo 任务信息
     * @param address  地址
     * @param response 返回请求
     */
    void recordTaskLog(TaskInfo taskInfo, String address, TaskResponse response);
}
