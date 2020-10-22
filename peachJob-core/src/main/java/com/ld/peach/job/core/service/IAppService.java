package com.ld.peach.job.core.service;

import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.params.RegistryParam;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @InterfaceName IJobService
 * @Description TODO
 * @Author lidong
 * @Date 2020/9/28
 * @Version 1.0
 */
public interface IAppService {

    /**
     * 节点注册
     *
     * @param registryParam 注册参数
     * @return
     */
    boolean registry(RegistryParam registryParam);

    /**
     * 待调度任务列表
     * 时间间隔单位是分钟
     *
     * @param timeInterVal 时间间隔
     * @return 未执行任务列表
     */
    List<TaskInfo> getUnExecutedTaskList(int timeInterVal);

    /**
     * 根据 任务ID 获取任务信息对象
     *
     * @param id 任务 ID
     * @return
     */
    TaskInfo getTaskInfoById(Long id);

    /**
     * 根据 任务ID 更新任务信息
     *
     * @param taskInfo 任务信息对象
     * @return
     */
    boolean updateTaskInfoById(TaskInfo taskInfo);

    /**
     * 使用线程本地变量记录锁的持有者
     */
    ThreadLocal<String> ownerThreadLocal = new ThreadLocal<>();

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁 KEY
     * @return 返回true代表已经获得锁，false代表获取锁失败（锁已经被别的进程占有）
     */
    default boolean tryLock(String lockKey) {
        String owner = ownerThreadLocal.get();
        if (Objects.nonNull(owner) && !owner.equals(TaskConstant.OPERATION_TRY_LOCK)) {
            // already hold a lock
            return true;
        }
        ownerThreadLocal.set(TaskConstant.OPERATION_TRY_LOCK);
        owner = UUID.randomUUID().toString();
        if (tryLock(lockKey, owner)) {
            ownerThreadLocal.set(owner);
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
            String owner = ownerThreadLocal.get();
            if (null == owner) {
                throw new IllegalMonitorStateException("should not call unlock() without tryLock(()");
            }
            ownerThreadLocal.remove();
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
     * @return 影响行数
     */
    default int cleanTimeoutApp() {
        return removeTimeOutApp(TaskConstant.CLEAN_TIMEOUT);
    }

    /**
     * 移除超时节点
     *
     * @param timeout 超时时长
     * @return
     */
    int removeTimeOutApp(int timeout);

    /**
     * 移除节点
     *
     * @param registryParam 注册参数
     * @return
     */
    boolean removeApp(RegistryParam registryParam);


    /**
     * 查询注册地址列表
     *
     * @param app 客户端 APP 名称
     * @return
     */
    List<String> getAppAddressList(String app);
}
