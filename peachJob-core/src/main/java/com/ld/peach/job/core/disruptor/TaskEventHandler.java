package com.ld.peach.job.core.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @ClassName TaskEventHandler
 * @Description TODO
 * @Author lidong
 * @Date 2020/10/22
 * @Version 1.0
 */
public class TaskEventHandler implements EventHandler<TaskEvent> {


    @Override
    public void onEvent(TaskEvent event, long sequence, boolean endOfBatch) throws Exception {

    }
}
