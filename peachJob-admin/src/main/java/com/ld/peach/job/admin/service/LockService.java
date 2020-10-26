package com.ld.peach.job.admin.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ld.peach.job.admin.mapper.LockInfoMapper;
import com.ld.peach.job.core.model.LockInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName LockService
 * @Description 锁服务层
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Slf4j
@Service
public class LockService {

    @Resource
    private LockInfoMapper lockInfoMapper;


    public int insert(String name, String owner) {
        return lockInfoMapper.insert(new LockInfo().setName(name).setOwner(owner).setCreateTime(new Date()));
    }

    public int delete(String name, String owner) {
        return lockInfoMapper.delete(Wrappers.<LockInfo>lambdaQuery().eq(LockInfo::getName, name)
                .eq(Objects.nonNull(owner), LockInfo::getOwner, owner));
    }
}
