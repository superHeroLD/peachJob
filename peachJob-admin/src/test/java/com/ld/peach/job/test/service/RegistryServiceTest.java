package com.ld.peach.job.test.service;

import com.ld.peach.job.admin.service.RegistryService;
import com.ld.peach.job.core.constant.TaskConstant;
import com.ld.peach.job.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @ClassName RegistryServiceTest
 * @Description 注册服务测试类
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
public class RegistryServiceTest extends BaseTest {

    @Resource
    private RegistryService registryService;

    @Test
    public void removeTimeOutTest() {
        int removeNum = registryService.removeTimeOut(TaskConstant.CLEAN_TIMEOUT);

        log.info("removeTimeOutTest remove time service num: {}", removeNum);
    }
}
