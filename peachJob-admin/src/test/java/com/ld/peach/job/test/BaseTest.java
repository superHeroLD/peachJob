package com.ld.peach.job.test;

import com.ld.preach.job.admin.PeachJobAdminApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName BaseTest
 * @Description 基础测试类
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@SpringBootTest(classes = PeachJobAdminApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {
}
