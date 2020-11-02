package com.ld.peach.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.peach.job.core.model.TaskLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName TaskLogMapper
 * @Description 任务Log
 * @Author lidong
 * @Date 2020/11/2
 * @Version 1.0
 */
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {
}
