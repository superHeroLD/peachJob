package com.ld.peach.job.admin.service;

import com.ld.peach.job.admin.mapper.TaskInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName TaskService
 * @Description 任务service
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Slf4j
@Service
public class TaskService {

    @Resource
    private TaskInfoMapper taskInfoMapper;
}
