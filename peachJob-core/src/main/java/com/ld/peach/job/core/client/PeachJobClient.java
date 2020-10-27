package com.ld.peach.job.core.client;

import cn.hutool.core.collection.CollectionUtil;
import com.ld.peach.job.core.exception.PeachTaskException;
import com.ld.peach.job.core.executor.AbstractTaskExecutor;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.service.IAdminService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName PeachJobClient
 * @Description PeachJob Client
 * @Author lidong
 * @Date 2020/10/27
 * @Version 1.0
 */
@Slf4j
public class PeachJobClient {

    /**
     * 发布任务
     *
     * @param taskInfo 任务信息
     * @return 是否成功
     * @throws PeachTaskException 任务异常
     */
    public static boolean publishTask(TaskInfo taskInfo) throws PeachTaskException {
        if (Objects.isNull(taskInfo)) {
            return true;
        }

        List<IAdminService> adminServiceList = AbstractTaskExecutor.getAdminServiceList();
        if (CollectionUtil.isEmpty(adminServiceList)) {
            throw new PeachTaskException("Can't find any peach job admin service");
        }

        //路由算法
        IAdminService adminService;
        if (adminServiceList.size() == 1) {
            adminService = adminServiceList.get(0);
        } else {
            int adminNum = adminServiceList.size();
            int position = ThreadLocalRandom.current().nextInt(adminNum);
            adminService = adminServiceList.get(position);
        }

        if (Objects.isNull(adminService)) {
            throw new PeachTaskException("adminService is null");
        }

        //发送请求
        int sendNum = adminService.publishTask(taskInfo);

        if (sendNum != 1 && adminServiceList.size() > 1) {
            for (IAdminService tmpAdminService : adminServiceList) {
                if (tmpAdminService.equals(adminService)) {
                    continue;
                }

                int num = tmpAdminService.publishTask(taskInfo);
                if (num == 1) {
                    sendNum = 1;
                    break;
                }
            }
        }

        if (sendNum != 1) {
            throw new PeachTaskException(String.format("Fail to publish task: [%s]", taskInfo));
        }

        log.info("send success task: {} ", taskInfo);
        return true;
    }

    /**
     * 这个方法没写完
     * 主要是因为批量发送会有问题，如果部分成功了，怎么处理失败的呢？主要是不知道哪些失败了
     *
     * @param taskInfoList 任务列表
     * @return 成功发送数量
     * @throws PeachTaskException 异常
     */
    @Deprecated
    private int batchPublishTask(List<TaskInfo> taskInfoList) throws PeachTaskException {
        if (CollectionUtil.isEmpty(taskInfoList)) {
            return 0;
        }

        List<IAdminService> adminServiceList = AbstractTaskExecutor.getAdminServiceList();
        if (CollectionUtil.isEmpty(adminServiceList)) {
            throw new PeachTaskException("Can't find any peach job admin service");
        }

        //路由算法
        IAdminService adminService;
        if (adminServiceList.size() == 1) {
            adminService = adminServiceList.get(0);
        } else {
            int adminNum = adminServiceList.size();
            int position = ThreadLocalRandom.current().nextInt(adminNum);
            adminService = adminServiceList.get(position);
        }

        if (Objects.isNull(adminService)) {
            throw new PeachTaskException("adminService is null");
        }
        //发送请求
        int sendNum = adminService.batchPublishTask(taskInfoList);

        //TODO 这里后面的逻辑有问题
        if (sendNum != taskInfoList.size()) {
            throw new PeachTaskException(String.format("success send task num: [%s] not equals need send task num: [%s]", sendNum, taskInfoList.size()));
        }

        return sendNum;
    }
}
