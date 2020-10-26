package com.ld.peach.job.admin.service.impl;

import com.ld.peach.job.admin.service.LockService;
import com.ld.peach.job.admin.service.RegistryService;
import com.ld.peach.job.admin.service.TaskService;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.params.RegistryParam;
import com.ld.peach.job.core.service.IAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName TaskService
 * @Description 任务服务接口实现类
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Slf4j
@Service
public class AppServiceImpl implements IAppService {

    @Resource
    private TaskService taskService;
    @Resource
    private RegistryService registryService;
    @Resource
    private LockService lockService;

    /**
     * 服务注册
     *
     * @param registryParam 注册参数
     * @return 是否注册成功
     */
    @Override
    public boolean registry(RegistryParam registryParam) {
        return registryService.registry(registryParam);
    }

    @Override
    public List<TaskInfo> getUnExecutedTaskList(int timeInterVal) {
        return taskService.getUnExecutedTaskList(timeInterVal);
    }


    @Override
    public TaskInfo getTaskInfoById(Long id) {
        return null;
    }

    @Override
    public boolean updateTaskInfoById(TaskInfo taskInfo) {
        return taskService.updateTaskInfoById(taskInfo);
    }

    @Override
    public int batchUpdateTaskInfoById(List<TaskInfo> taskInfoList) {
        return taskService.batchUpdateTaskInfoById(taskInfoList);
    }

    @Override
    public boolean tryLock(String name, String owner) {
        return lockService.insert(name, owner) > 0;
    }

    @Override
    public boolean unlock(String name, String owner) {
        return lockService.delete(name, owner) > 0;
    }

    @Override
    public int removeTimeOutApp(int timeout) {
        return registryService.removeTimeOut(timeout);
    }

    @Override
    public boolean removeApp(RegistryParam registryParam) {
        return false;
    }

    @Override
    public List<String> getAppAddressList() {
        return registryService.listAvailableServiceList();
    }
}
