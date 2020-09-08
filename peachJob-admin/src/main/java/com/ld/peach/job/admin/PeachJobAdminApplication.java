package com.ld.peach.job.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName PeachJobAdminApplication
 * @Description Spring 启动类
 * @Author lidong
 * @Date 2020/9/7
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.ld.peach.job.admin.mapper")
public class PeachJobAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeachJobAdminApplication.class, args);
    }
}
