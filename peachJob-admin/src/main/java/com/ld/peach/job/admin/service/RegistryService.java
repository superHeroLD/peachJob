package com.ld.peach.job.admin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ld.peach.job.admin.mapper.ServiceRegistryMapper;
import com.ld.peach.job.core.constant.RegisterStatusEnum;
import com.ld.peach.job.core.model.ServiceRegistry;
import com.ld.peach.job.core.model.params.RegistryParam;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ServiceRegistryService
 * @Description 服务注Service
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Slf4j
@Service
public class RegistryService {

    @Resource
    private ServiceRegistryMapper serviceRegistryMapper;

    /**
     * 删除超时的注册app信息
     *
     * @param timeout 超时时间间隔
     * @return 更新数量
     */
    public int removeTimeOut(int timeout) {
        int updateNum = serviceRegistryMapper.update(ServiceRegistry.builder().status(RegisterStatusEnum.DISABLED.getValue()).build(), Wrappers.<ServiceRegistry>lambdaQuery()
                .eq(ServiceRegistry::getStatus, RegisterStatusEnum.ENABLED.getValue()).le(ServiceRegistry::getUpdateTime, DateUtil.offset(new Date(), DateField.MILLISECOND, -timeout)));
        if (updateNum > 0) {
            log.info("remove time out registry service num: {}", updateNum);
        }

        return updateNum;
    }

    /**
     * 服务节点注册
     *
     * @param registryParam 注册参数
     * @return 是否注册成功
     */
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

    /**
     * 获取可用服务节点列表
     *
     * @return 地址
     */
    public List<String> listAvailableServiceList() {
        List<ServiceRegistry> serviceList = serviceRegistryMapper
                .selectList(Wrappers.<ServiceRegistry>lambdaQuery().eq(ServiceRegistry::getStatus, RegisterStatusEnum.ENABLED.getValue()));

        if (CollectionUtil.isEmpty(serviceList)) {
            return Collections.emptyList();
        }

        return serviceList.stream().map(serviceRegistry -> serviceRegistry.getAddress().trim())
                .filter(StringUtil::isNotBlank).distinct().collect(Collectors.toList());
    }
}
