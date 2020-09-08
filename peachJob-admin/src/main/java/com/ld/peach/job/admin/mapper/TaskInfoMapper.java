package com.ld.peach.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.peach.job.core.model.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @InterfaceName TaskInfoMapper
 * @Description 任务信息Dao Mapper
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {
}
