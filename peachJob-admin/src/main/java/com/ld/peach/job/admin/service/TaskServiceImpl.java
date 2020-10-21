package com.ld.peach.job.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ld.peach.job.admin.mapper.ServiceRegistryMapper;
import com.ld.peach.job.core.model.ServiceRegistry;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.model.params.RegistryParam;
import com.ld.peach.job.core.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
public class TaskServiceImpl implements ITaskService {

    @Resource
    private ServiceRegistryMapper serviceRegistryMapper;

    /**
     * 服务注册
     *
     * @param registryParam 注册参数
     * @return 是否注册成功
     */
    @Override
    public boolean registry(RegistryParam registryParam) {
        log.info("receive registry params: {}", registryParam);

        int updateNum = serviceRegistryMapper.update(ServiceRegistry.builder()
                        .appName(registryParam.getApp())
                        .address(registryParam.getAddress())
                        .status(registryParam.getRegisterStatusEnum().getValue())
                        .updateTime(new Date()).build(),
                Wrappers.<ServiceRegistry>lambdaQuery().eq(ServiceRegistry::getAppName, registryParam.getApp()).eq(ServiceRegistry::getAddress, registryParam.getAddress()));
        log.info("update service registry info num: {}", updateNum);

        if (updateNum < 1) {
            //没有则存入注册信息
            int insertNum = serviceRegistryMapper.insert(ServiceRegistry.builder()
                    .appName(registryParam.getApp())
                    .address(registryParam.getAddress())
                    .status(registryParam.getRegisterStatusEnum().getValue())
                    .build());

            log.info("insert service registry info num: {}", insertNum);
            return insertNum == 1;
        }

        return true;
    }

    @Override
    public List<TaskInfo> getTaskInfoList(long nextTime) {
        return null;
    }

    @Override
    public TaskInfo getTaskInfoById(Long id) {
        return null;
    }

    @Override
    public boolean updateTaskInfoById(TaskInfo taskInfo) {
        return false;
    }

    @Override
    public boolean tryLock(String name, String owner) {
        return false;
    }

    @Override
    public boolean unlock(String name, String owner) {
        return false;
    }

    @Override
    public int removeTimeOutApp(int timeout) {
        return 0;
    }

    @Override
    public boolean removeApp(RegistryParam registryParam) {
        return false;
    }

    @Override
    public List<String> getAppAddressList(String app) {
        return null;
    }
}
