package com.ld.peach.job.test;

import com.ld.peach.job.core.mapper.TaskInfoMapper;
import com.ld.peach.job.core.model.TaskInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
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
@RunWith(SpringRunner.class)
public class MapperTest extends BaseTest {

    @Resource
    private TaskInfoMapper taskInfoMapper;

    @Test
    public void testSelect() {
        System.out.println("----- selectAll method test ------");
        List<TaskInfo> taskInfoList = taskInfoMapper.selectList(null);
        taskInfoList.forEach(System.out::println);
    }
}
