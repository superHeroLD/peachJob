package com.ld.peach.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.peach.job.core.model.ServiceRegistry;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName ServiceRegistryMapper
 * @Description 服务注册 Dao Mapper
 * @Author lidong
 * @Date 2020/10/21
 * @Version 1.0
 */
@Mapper
public interface ServiceRegistryMapper extends BaseMapper<ServiceRegistry> {
}
