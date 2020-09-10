package com.ld.peach.job.core.model.builder;

import com.ld.peach.job.core.constant.JobConstant;
import com.ld.peach.job.core.model.TaskInfo;
import com.ld.peach.job.core.util.DateUtil;
import com.ld.peach.job.core.util.StringUtil;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.ld.peach.job.core.util.CheckUtil.checkState;

/**
 * @ClassName PeachTaskBuilder
 * @Description task builder
 * @Author lidong
 * @Date 2020/9/10
 * @Version 1.0
 */
public final class PeachTaskBuilder {

    private PeachTaskBuilder() {

    }

    /**
     * 执行时间
     */
    private Date estimatedExecutionTime;

    /**
     * 任务执行器名称
     */
    private String taskHandler;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 执行参数
     */
    private String executeParams;

    /**
     * 最大重试次数
     */
    private Integer maxRetryNum;

    /**
     * 执行策略
     */
    private Short executionStrategy;

    public static PeachTaskBuilder newBuilder() {
        return new PeachTaskBuilder();
    }

    public PeachTaskBuilder taskHandler(String taskHandlerName) {
        checkState(StringUtil.isNotBlank(taskHandlerName), "taskHandlerName must not blank");

        this.taskHandler = taskHandlerName;
        return this;
    }

    public PeachTaskBuilder executeParams(String params) {
        checkState(StringUtil.isNotBlank(params), "params must not blank");

        this.executeParams = params;
        return this;
    }

    public PeachTaskBuilder maxRetryNum(Integer num) {
        checkState(Objects.nonNull(num) && num > 0, "num value range is error");

        this.maxRetryNum = num;
        return this;
    }

    public PeachTaskBuilder taskName(String taskName) {
        checkState(StringUtil.isNotBlank(taskName), "taskName must not blank");

        this.taskName = taskName;
        return this;
    }

    /**
     * 设置执行日期
     * 是一个具体的未来时间点
     * 目前不支持太小的时间单位，太小的时间单位不能用目前的架构来实现
     * 最小的时间单位是秒，最大的是天
     * <p>
     * 必要参数
     *
     * @param offset   时间便宜量
     * @param timeUnit 时间单位
     * @return WorkTaskBuilder 构造器
     */
    public PeachTaskBuilder executionDate(int offset, TimeUnit timeUnit) {
        checkState(offset > 0, "offset time must greater than zero");
        checkState(Objects.nonNull(timeUnit), "timeUnit can't be null");

        Date date;

        switch (timeUnit) {
            case NANOSECONDS:
                throw new RuntimeException("Not support nano seconds execution date");
            case MICROSECONDS:
                throw new RuntimeException("Not support micro seconds execution date");
            case MILLISECONDS:
                throw new RuntimeException("Not support millis seconds execution date");
            case SECONDS:
                date = DateUtil.offsetSecond(new Date(), offset);
                break;
            case MINUTES:
                date = DateUtil.offsetMinute(new Date(), offset);
                break;
            case HOURS:
                date = DateUtil.offsetHour(new Date(), offset);
                break;
            case DAYS:
                date = DateUtil.offsetDay(new Date(), offset);
                break;
            default:
                throw new RuntimeException("Not support this type of TimeUnit [" + timeUnit + "]");
        }

        this.estimatedExecutionTime = date;

        return this;
    }

    /**
     * 构建Task
     */
    public TaskInfo build() {
        return doBuild(this);
    }

    private static TaskInfo doBuild(PeachTaskBuilder builder) {
        checkBuilder(builder);

        final Date estimatedExecutionTime = builder.estimatedExecutionTime;
        final String taskHandler = builder.taskHandler;
        final String taskName = builder.taskName;
        final String executeParams = builder.executeParams;
        final Integer maxRetryNum = Objects.nonNull(builder.maxRetryNum) ? builder.maxRetryNum : JobConstant.MAX_RETRY_NUM;
        final Short executionStrategy = builder.executionStrategy;

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setEstimatedExecutionTime(estimatedExecutionTime);
        taskInfo.setTaskHandler(taskHandler);
        taskInfo.setTaskName(taskName);
        taskInfo.setExecuteParams(executeParams);
        taskInfo.setMaxRetryNum(maxRetryNum);
        taskInfo.setExecutionStrategy(executionStrategy);

        return taskInfo;
    }

    /**
     * 校验必要参数
     */
    private static void checkBuilder(PeachTaskBuilder builder) {
        checkState(Objects.nonNull(builder.estimatedExecutionTime), "estimatedExecutionTime can't be null");
        checkState(StringUtil.isNotBlank(builder.taskHandler), "taskHandlerName must not blank");
        checkState(StringUtil.isNotBlank(builder.taskName), "task name must not blank");
    }

}
