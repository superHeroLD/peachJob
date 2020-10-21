package com.ld.peach.job.test.mapper;

import com.ld.peach.job.admin.mapper.ServiceRegistryMapper;
import com.ld.peach.job.admin.mapper.TaskInfoMapper;
import com.ld.peach.job.core.model.ServiceRegistry;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MapperTest
 * @Description 测试Mapper
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
public class MapperTest extends BaseTest {

    @Resource
    private TaskInfoMapper taskInfoMapper;

    @Resource
    private ServiceRegistryMapper serviceRegistryMapper;

    @Test
    public void testSelect() {
        System.out.println("----- selectAll method test ------");
        List<TaskInfo> taskInfoList = taskInfoMapper.selectList(null);
        taskInfoList.forEach(System.out::println);
    }

    @Test
    public void serviceRegistryMapperTest() {
        List<ServiceRegistry> serviceRegistryList = serviceRegistryMapper.selectList(null);
        log.info("serviceRegistryList: {}", serviceRegistryList);
    }
}
