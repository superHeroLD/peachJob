package com.ld.peach.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.peach.job.core.model.LockInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName LockInfoMapper
 * @Description 锁 mapper
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Mapper
public interface LockInfoMapper extends BaseMapper<LockInfo> {
}
