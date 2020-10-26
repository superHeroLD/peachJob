package com.ld.peach.job.core.dispatch;

import cn.hutool.core.collection.CollectionUtil;
import com.ld.peach.job.core.constant.task.TaskExecutionStatus;
import com.ld.peach.job.core.exception.helper.ExceptionHelper;
import com.ld.peach.job.core.executor.ITaskExecutor;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.generic.param.DispatchParam;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.service.PeachJobHelper;
import com.ld.peach.job.core.starter.TaskScheduler;
import com.ld.peach.job.core.util.HashUtil;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName TaskDispatch
 * @Description 任务分发中心
 * @Author lidong
 * @Date 2020/10/26
 * @Version 1.0
 */
@Slf4j
public class TaskDispatchCenter {

    /**
     * 处理任务
     *
     * @param taskInfo 任务信息
     * @return 是否处理成功
     */
    public static boolean processTask(TaskInfo taskInfo) {
        if (Objects.isNull(taskInfo)) {
            return false;
        }

        //获取所有可用服务节点地址
        List<String> appAddressList = PeachJobHelper.getAppService().getAppAddressList();
        if (CollectionUtil.isEmpty(appAddressList)) {
            log.error("[TaskDispatchCenter] no service address available, dispatch task id: [{}] fail", taskInfo.getId());
            return false;
        }

        //路由算法-简单Hash
        String address;
        if (appAddressList.size() == 1) {
            address = appAddressList.get(0);
        } else {
            int position = HashUtil.hash(taskInfo.getId()) % appAddressList.size();
            address = appAddressList.get(position);
        }

        if (StringUtil.isBlank(address)) {
            log.error("[TaskDispatchCenter] no service address available, dispatch task id: [{}] fail", taskInfo.getId());
            return false;
        }

        DispatchParam dispatchParam = new DispatchParam().setHandler(taskInfo.getTaskHandler())
                .setParam(taskInfo.getExecuteParams())
                .setTaskId(taskInfo.getId());

        TaskResponse response = runExecutor(dispatchParam, address, taskInfo, appAddressList, taskInfo.getMaxRetryNum(), taskInfo.getExecutionTimes());


        return response.isSuccess();
    }


    private static TaskResponse runExecutor(DispatchParam dispatchParam, String address, TaskInfo taskInfo,
                                            List<String> appAddressList, int maxRetryCount, int actualRetryCount) {

        TaskResponse response;
        try {
            ITaskExecutor taskExecutor = TaskScheduler.getTaskExecutor(address);
            if (Objects.isNull(taskExecutor)) {
                log.error("[runExecutor] can't find any taskExecutor by address: {}", address);
                return TaskResponse.fail(String.format("can't find any taskExecutor by address: [{%s}]", address));
            }

            response = taskExecutor.run(dispatchParam);
        } catch (Exception ex) {
            response = TaskResponse.fail(ExceptionHelper.getErrorInfo(ex));
        }

        if (response.isSuccess()) {
            //更新任务执行状态
            taskInfo.setStatus(TaskExecutionStatus.SUCCESS.getCode());
        } else {
            taskInfo.setStatus(TaskExecutionStatus.FAIL.getCode());
        }

        taskInfo.setExecutionTimes(taskInfo.getExecutionTimes() + 1);
        PeachJobHelper.getAppService().updateTaskInfoById(taskInfo);

        //TODO 在这里重试还是在调度一次重试呢？

        return response;
    }

}
